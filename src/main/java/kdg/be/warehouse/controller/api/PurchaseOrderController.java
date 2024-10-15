package kdg.be.warehouse.controller.api;

import kdg.be.warehouse.controller.dto.PurchaseOrderDTO;
import kdg.be.warehouse.controller.dto.mapper.PurchaseOrderMapper;
import kdg.be.warehouse.service.PurchaseOrderService;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    private final PurchaseOrderMapper purchaseOrderMapper = PurchaseOrderMapper.INSTANCE;

    @PostMapping("/receive")
    public void receivePurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder purchaseOrder = purchaseOrderMapper.toEntity(purchaseOrderDTO);
        purchaseOrderService.savePurchaseOrder(purchaseOrder);
        System.out.println("Received Purchase Order: " + purchaseOrder);
    }
}