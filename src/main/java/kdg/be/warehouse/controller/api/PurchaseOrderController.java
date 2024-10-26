package kdg.be.warehouse.controller.api;

import kdg.be.warehouse.controller.dto.CustomerDTO;
import kdg.be.warehouse.controller.dto.OrderLineDTO;
import kdg.be.warehouse.controller.dto.PurchaseOrderDTO;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    @PostMapping("/receive")
    public void receivePurchaseOrder(@RequestBody PurchaseOrderDTO purchaseOrderDTO) {
        PurchaseOrder purchaseOrder = convertToEntity(purchaseOrderDTO);
        purchaseOrderService.savePurchaseOrder(purchaseOrder);
    }

    @PostMapping("/complete")
    public Map<String, Object> completePurchaseOrders(@RequestParam String sellerId, @RequestBody List<String> poNumbers) {
        List<String> errors = purchaseOrderService.completePurchaseOrders(UUID.fromString(sellerId), poNumbers);
        return Map.of("errors", errors);
    }

    @GetMapping("/open")
    ResponseEntity<List<PurchaseOrderDTO>> getOpenPurchaseOrders() {
        List<PurchaseOrder> openPurchaseOrders = purchaseOrderService.getOpenPurchaseOrders();
        List<PurchaseOrderDTO> openPurchaseOrderDTOs = openPurchaseOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(openPurchaseOrderDTOs);
    }

    @GetMapping("/completed")
    ResponseEntity<List<PurchaseOrderDTO>> getCompletedPurchaseOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        List<PurchaseOrder> completedPurchaseOrders = purchaseOrderService.getCompletedPurchaseOrders(startDate, endDate);
        List<PurchaseOrderDTO> completedPurchaseOrderDTOs = completedPurchaseOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(completedPurchaseOrderDTOs);
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

    private PurchaseOrderDTO convertToDTO(PurchaseOrder purchaseOrder) {
        PurchaseOrderDTO dto = new PurchaseOrderDTO(
                purchaseOrder.getPoNumber(),
                purchaseOrder.getReferenceUUID().toString(),
                new CustomerDTO(
                        purchaseOrder.getBuyer().getCustomerId().toString(),
                        purchaseOrder.getBuyer().getName(),
                        purchaseOrder.getBuyer().getAddress()
                ),
                new CustomerDTO(
                        purchaseOrder.getSeller().getCustomerId().toString(),
                        purchaseOrder.getSeller().getName(),
                        purchaseOrder.getSeller().getAddress()
                ),
                purchaseOrder.getVesselNumber(),
                purchaseOrder.getOrderLines().stream()
                        .map(this::convertOrderLineEntityToDTO)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    private OrderLineDTO convertOrderLineEntityToDTO(OrderLine orderLine) {
        OrderLineDTO dto = new OrderLineDTO(
                orderLine.getLineNumber(),
                orderLine.getMaterialName(),
                orderLine.getQuantity(),
                orderLine.getUom()
        );
        return dto;
    }
}