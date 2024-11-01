package kdg.be.warehouse.controller.api;

import jakarta.validation.Valid;
import kdg.be.warehouse.controller.dto.PurchaseOrderDTO;
import kdg.be.warehouse.controller.dto.mapper.PurchaseOrderMapper;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.exceptions.InvalidSellerOrMaterialException;
import kdg.be.warehouse.exceptions.POConflictException;
import kdg.be.warehouse.service.PurchaseOrderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;
    private final PurchaseOrderMapper purchaseorderMapper;

    public PurchaseOrderController(PurchaseOrderService purchaseOrderService, PurchaseOrderMapper purchaseorderMapper) {
        this.purchaseOrderService = purchaseOrderService;
        this.purchaseorderMapper = purchaseorderMapper;
    }

    @PostMapping("/receive")
    public ResponseEntity<Void> receivePurchaseOrder(@RequestBody @Valid PurchaseOrderDTO purchaseOrderDTO) {
        try {
            purchaseOrderService.savePurchaseOrder(purchaseorderMapper.purchaseOrder(purchaseOrderDTO));
            return ResponseEntity.status(201).build();
        } catch (POConflictException e) {
            return ResponseEntity.status(409).build();
        } catch (InvalidSellerOrMaterialException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completePurchaseOrders(@RequestParam String sellerId, @RequestBody List<String> poNumbers) {
        List<String> errors = purchaseOrderService.completePurchaseOrders(UUID.fromString(sellerId), poNumbers);
        if (errors.isEmpty()) {
            return ResponseEntity.ok(Map.of("errors", errors));
        } else {
            return ResponseEntity.status(400).body(Map.of("errors", errors));
        }
    }

    @GetMapping("/open")
    public ResponseEntity<List<PurchaseOrderDTO>> getOpenPurchaseOrders() {
        List<PurchaseOrder> openPurchaseOrders = purchaseOrderService.getOpenPurchaseOrders();
        List<PurchaseOrderDTO> openPurchaseOrderDTOs = openPurchaseOrders.stream()
                .map(purchaseorderMapper::purchaseOrderDTO)
                .toList();
        return ResponseEntity.ok(openPurchaseOrderDTOs);
    }

    @GetMapping("/completed")
    ResponseEntity<List<PurchaseOrderDTO>> getCompletedPurchaseOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate) {
        
        List<PurchaseOrder> completedPurchaseOrders = purchaseOrderService.getCompletedPurchaseOrders(startDate, endDate);
        List<PurchaseOrderDTO> completedPurchaseOrderDTOs = completedPurchaseOrders.stream()
                .map(purchaseorderMapper::purchaseOrderDTO)
                .toList();
        return ResponseEntity.ok(completedPurchaseOrderDTOs);
    }
}