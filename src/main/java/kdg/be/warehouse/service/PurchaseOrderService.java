package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.repository.CustomerRepository;
import kdg.be.warehouse.repository.MaterialRepository;
import kdg.be.warehouse.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    //TODO: check if user has warehouse of material
    public List<String> completePurchaseOrders(UUID sellerId, List<String> poNumbers) {
        List<String> errors = List.of();
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
        PurchaseOrder purchaseOrder = findPurchaseOrder(sellerId, poNumber);
        List<OrderLine> orderLines = purchaseOrder.getOrderLines();
        Customer seller = findSeller(sellerId);

        for (OrderLine orderLine : orderLines) {
            Material material = findMaterial(orderLine.getMaterialName());
            processOrderLine(seller, material, orderLine);
        }

        invoiceSeller(seller, purchaseOrder);
    }

    private void invoiceSeller(Customer seller, PurchaseOrder po) {
        commissionService.addCommissionInvoice(seller, po);
    }

    private PurchaseOrder findPurchaseOrder(UUID sellerId, String poNumber) {
        return purchaseOrderRepository.findByPoNumberAndSeller_customerId(poNumber, sellerId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found"));
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
        Customer buyer = findOrCreateCustomer(purchaseOrder.getBuyer());
        Customer seller = findOrCreateCustomer(purchaseOrder.getSeller());

        purchaseOrder.setBuyer(buyer);
        purchaseOrder.setSeller(seller);

        for (OrderLine orderLine : purchaseOrder.getOrderLines()) {
            orderLine.setPurchaseOrder(purchaseOrder);
        }

        return purchaseOrderRepository.save(purchaseOrder);
    }

    private Customer findOrCreateCustomer(Customer customer) {
        if (customer == null) {
            return null;
        }
        return customerRepository.findById(customer.getCustomerId())
                .orElseGet(() -> customerRepository.save(customer));
    }

}