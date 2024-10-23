package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.repository.CustomerRepository;
import kdg.be.warehouse.repository.MaterialRepository;
import kdg.be.warehouse.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseService warehouseService;
    private final MaterialRepository materialRepository;

    public PurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository, CustomerRepository customerRepository, WarehouseService warehouseService, MaterialRepository materialRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.customerRepository = customerRepository;
        this.warehouseService = warehouseService;
        this.materialRepository = materialRepository;
    }

    //TODO: check if user has warehouse of material
    public List<String> completePurchaseOrders(UUID sellerId, List<String> poNumbers) {
        List<String> errors = List.of();
        for (String poNumber : poNumbers) {
            try {
                completePurchaseOrder(sellerId, poNumber);
            } catch (RuntimeException e) {
                errors.add(e.getMessage());
            }
        }
        return errors;
    }

    public void completePurchaseOrder(UUID sellerId, String poNumber) {
        PurchaseOrder purchaseOrder = findPurchaseOrder(sellerId, poNumber);
        List<OrderLine> orderLines = purchaseOrder.getOrderLines();

        for (OrderLine orderLine : orderLines) {
            Material material = findMaterial(orderLine.getMaterialName());
            Customer seller = findSeller(sellerId);
            processOrderLine(seller, material, orderLine);
        }
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
        Customer buyer = purchaseOrder.getBuyer();
        if (buyer != null && buyer.getCustomerId() == null) {
            customerRepository.save(buyer);
        }

        Customer seller = purchaseOrder.getSeller();
        if (seller != null && seller.getCustomerId() == null) {
            customerRepository.save(seller);
        }

        for (OrderLine orderLine : purchaseOrder.getOrderLines()) {
            orderLine.setPurchaseOrder(purchaseOrder);
        }

        return purchaseOrderRepository.save(purchaseOrder);
    }
}