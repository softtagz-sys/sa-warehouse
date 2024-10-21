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

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private WarehouseService warehouseService;
    @Autowired
    private MaterialRepository materialRepository;

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

    //TODO: Refactor this to lower complexity
    public void completePurchaseOrder(UUID sellerId, String poNumber) {
        Optional<PurchaseOrder> optionalPurchaseOrder = purchaseOrderRepository.findByPoNumberAndSeller_customerId(poNumber, sellerId);
        if (optionalPurchaseOrder.isPresent()) {
            PurchaseOrder purchaseOrder = optionalPurchaseOrder.get();
            List<OrderLine> orderlines = purchaseOrder.getOrderLines();
            for (OrderLine orderLine : orderlines) {
                Optional<Material> optionalMaterial = materialRepository.findByName(orderLine.getMaterialName());
                if (optionalMaterial.isPresent()) {
                    Material material = optionalMaterial.get();
                    Optional<Customer> optionalSeller = customerRepository.findById(sellerId);
                    if (optionalSeller.isPresent() && warehouseService.hasWarehouseOfMaterial(optionalSeller.get(), material)) {
                        Warehouse warehouse = warehouseService.getWarehouseOfMaterialFromCustomer(optionalSeller.get(), material);
                        warehouseService.retrieveMaterial(warehouse, material, orderLine.getQuantity());
                    } else {
                        throw new RuntimeException("Seller does not exist or does not have a warehouse");
                    }
                } else {
                    throw new RuntimeException("Material not found: " + orderLine.getMaterialName());
                }
            }
        } else {
            throw new RuntimeException("Purchase Order not found");
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