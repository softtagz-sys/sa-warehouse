package kdg.be.warehouse.controller.api;

import kdg.be.warehouse.controller.dto.CustomerDTO;
import kdg.be.warehouse.controller.dto.OrderLineDTO;
import kdg.be.warehouse.controller.dto.PurchaseOrderDTO;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @PostMapping("/receive")
    public void receivePurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder purchaseOrder = convertToEntity(purchaseOrderDTO);
        purchaseOrderService.savePurchaseOrder(purchaseOrder);
    }

    //TODO: change to messaging
    @PostMapping("/complete")
    public Map<String, Object> completePurchaseOrders(@RequestParam String sellerId, @RequestBody List<String> poNumbers) {
        List<String> errors = purchaseOrderService.completePurchaseOrders(UUID.fromString(sellerId), poNumbers);
        return Map.of("errors", errors);
    }

    private PurchaseOrder convertToEntity(PurchaseOrderDTO purchaseOrderDTO) {
        Customer buyer = new Customer(
                UUID.fromString(purchaseOrderDTO.getCustomerParty().getUUID()),
                purchaseOrderDTO.getCustomerParty().getName(),
                purchaseOrderDTO.getCustomerParty().getAddress()
        );

        Customer seller = new Customer(
                UUID.fromString(purchaseOrderDTO.getSellerParty().getUUID()),
                purchaseOrderDTO.getSellerParty().getName(),
                purchaseOrderDTO.getSellerParty().getAddress()
        );

        List<OrderLine> orderLines = purchaseOrderDTO.getOrderLines().stream()
                .map(this::convertOrderLineDTOToEntity)
                .collect(Collectors.toList());

        return new PurchaseOrder(
                purchaseOrderDTO.getPoNumber(),
                UUID.fromString(purchaseOrderDTO.getReferenceUUID()),
                buyer,
                seller,
                purchaseOrderDTO.getVesselNumber(),
                orderLines
        );
    }

    private OrderLine convertOrderLineDTOToEntity(OrderLineDTO orderLineDTO) {
        OrderLine orderLine = new OrderLine();
        orderLine.setLineNumber(orderLineDTO.getLineNumber());
        orderLine.setMaterialName(orderLineDTO.getMaterialName());
        orderLine.setQuantity(orderLineDTO.getQuantity());
        orderLine.setUom(orderLineDTO.getUom());
        return orderLine;
    }
}