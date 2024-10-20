package kdg.be.warehouse.controller.api;


import kdg.be.warehouse.controller.dto.out.WarehouseStatusDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
public class WarehousesController {


    @GetMapping("/{customerId}/{rawMaterial}")
    public ResponseEntity<WarehouseStatusDto> getWarehouseStatus(@PathVariable String customerId, @PathVariable String rawMaterial) {
        // TODO add Service - Business Logic
        return ResponseEntity.ok(new WarehouseStatusDto("test", false));
    }
}
