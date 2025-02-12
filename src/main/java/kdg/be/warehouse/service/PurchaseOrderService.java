package kdg.be.warehouse.service;

import jakarta.validation.constraints.NotBlank;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.exceptions.InvalidSellerOrMaterialException;
import kdg.be.warehouse.exceptions.POConflictException;
import kdg.be.warehouse.repository.CustomerRepository;
import kdg.be.warehouse.repository.MaterialRepository;
import kdg.be.warehouse.repository.PurchaseOrderRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseService warehouseService;
    private final MaterialRepository materialRepository;
    private final CommissionService commissionService;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, CustomerRepository customerRepository, WarehouseService warehouseService, MaterialRepository materialRepository, CommissionService commissionService) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.customerRepository = customerRepository;
        this.warehouseService = warehouseService;
        this.materialRepository = materialRepository;
        this.commissionService = commissionService;
    }

    public List<String> completePurchaseOrders(UUID sellerId, List<String> poNumbers) {
        List<String> errors = new ArrayList<>();
        for (String poNumber : poNumbers) {
            try {
                completeAndInvoicePurchaseOrder(sellerId, poNumber);
            } catch (RuntimeException e) {
                errors.add(e.getMessage());
            }
        }
        return errors;
    }

    @Transactional
    public void completeAndInvoicePurchaseOrder(UUID sellerId, String poNumber) {
        PurchaseOrder purchaseOrder = findPurchaseOrder(poNumber, sellerId);
        List<OrderLine> orderLines = purchaseOrder.getOrderLines();
        Customer seller = findSeller(sellerId);

        for (OrderLine orderLine : orderLines) {
            Material material = findMaterial(orderLine.getMaterialName());
            processOrderLine(seller, material, orderLine);
        }

        purchaseOrder.setCompleted(true);
        purchaseOrder.setCompletedDate(new Date());
        purchaseOrderRepository.save(purchaseOrder);

        invoiceSeller(seller, purchaseOrder);
    }

    private void invoiceSeller(Customer seller, PurchaseOrder po) {
        commissionService.addCommissionInvoice(seller, po);
    }

    @Transactional
    protected PurchaseOrder findPurchaseOrder(String poNumber, UUID sellerId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findByPoNumberAndSeller_CustomerId(poNumber, sellerId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
        List<OrderLine> orderLines = purchaseOrderRepository.findOrderLinesByPurchaseOrderId(purchaseOrder.getId());
        purchaseOrder.setOrderLines(orderLines);
        return purchaseOrder;
    }

    private Material findMaterial(String materialName) {
        return materialRepository.findByNameIgnoreCase(materialName)
                .orElseThrow(() -> new RuntimeException("Material not found: " + materialName));
    }

    private Customer findSeller(UUID sellerId) {
        return customerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller does not exist"));
    }

    private void processOrderLine(Customer seller, Material material, OrderLine orderLine) {
        if (warehouseService.hasWarehouseOfMaterial(seller, material)) {
            Warehouse warehouse = warehouseService.getWarehouseOfMaterialFromCustomer(seller, material);
            warehouseService.retrieveMaterial(warehouse, material, orderLine.getQuantity());
        } else {
            throw new RuntimeException("Seller does not have a warehouse for the material");
        }
    }

    @Transactional
    public PurchaseOrder savePurchaseOrder(PurchaseOrder purchaseOrder) {
        // Validate if PO number is unique
        if (!isPoNumberUnique(purchaseOrder.getPoNumber())) {
            throw new POConflictException("PO number is not unique");
        }

        Customer buyer = findOrCreateCustomer(purchaseOrder.getBuyer());
        Customer seller = findOrCreateCustomer(purchaseOrder.getSeller());

        purchaseOrder.setBuyer(buyer);
        purchaseOrder.setSeller(seller);

        for (OrderLine orderLine : purchaseOrder.getOrderLines()) {
            if (!hasWarehouseOfMaterial(seller.getCustomerId(), orderLine.getMaterialName())) {
                throw new InvalidSellerOrMaterialException("Seller does not have warehouse of specific material");
            } else {
                orderLine.setPurchaseOrder(purchaseOrder);
            }
        }

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private boolean isPoNumberUnique(String poNumber) {
        return purchaseOrderRepository.findByPoNumber(poNumber).isEmpty();
    }

    private Customer findOrCreateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        return customerRepository.findById(customer.getCustomerId())
                .orElseGet(() -> customerRepository.save(customer));
    }

    public boolean hasWarehouseOfMaterial(UUID sellerId, @NotBlank String materialName) {
        Customer seller = customerRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        Material material = materialRepository.findByNameIgnoreCase(materialName)
                .orElseThrow(() -> new RuntimeException("Material not found"));
        return warehouseService.hasWarehouseOfMaterial(seller, material);
    }


    @Transactional(readOnly = true)
    public List<PurchaseOrder> getOpenPurchaseOrders() {
        return purchaseOrderRepository.findOpenPurchaseOrders();
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrder> getCompletedPurchaseOrders(Date startDate, Date endDate) {
        return purchaseOrderRepository.findAllByIsCompletedTrueAndCompletedDateBetween(startDate, endDate);
    }
}