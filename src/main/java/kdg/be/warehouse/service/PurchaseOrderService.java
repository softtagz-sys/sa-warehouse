package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.repository.CustomerRepository;
import kdg.be.warehouse.repository.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public void completePurchaseOrder(Long sellerId, String poNumber) {
        Optional<PurchaseOrder> optionalPurchaseOrder = purchaseOrderRepository.findByPoNumberAndSellerId(poNumber, sellerId);
        if (optionalPurchaseOrder.isPresent()) {
            PurchaseOrder purchaseOrder = optionalPurchaseOrder.get();
            // Logic to retrieve materials from warehouses
            purchaseOrderRepository.save(purchaseOrder);
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