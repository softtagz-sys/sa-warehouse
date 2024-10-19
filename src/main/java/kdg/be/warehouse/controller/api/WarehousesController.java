package kdg.be.warehouse.controller.api;

import kdg.be.warehouse.controller.dto.out.WarehouseStatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouses")
public class WarehousesController {

    @GetMapping("/{warehouseId}")
    public ResponseEntity<WarehouseStatusDto> getWarehouseStatus(@PathVariable String warehouseId) {

        // TODO add Service - Business Logic
        return ResponseEntity.ok(new WarehouseStatusDto(warehouseId, false));
    }
}
