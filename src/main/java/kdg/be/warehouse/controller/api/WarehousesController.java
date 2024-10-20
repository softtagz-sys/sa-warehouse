package kdg.be.warehouse.controller.api;


import kdg.be.warehouse.controller.dto.out.WarehouseStatusDto;
import kdg.be.warehouse.service.WarehouseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/warehouses")
public class WarehousesController {

    private final WarehouseService warehouseService;

    public WarehousesController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping("/{customerId}/{rawMaterial}")
    public ResponseEntity<WarehouseStatusDto> getWarehouseStatus(@PathVariable String customerId, @PathVariable String rawMaterial) {

        Optional<Boolean> warehouseAvailableForDelivery = warehouseService.GetAvailabilityStatus(UUID.fromString(customerId), rawMaterial);
        return warehouseAvailableForDelivery
                .map(available -> ResponseEntity.ok(new WarehouseStatusDto(available)))
                .orElseGet(() -> ResponseEntity.notFound().build());

    }
}
