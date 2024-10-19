package kdg.be.warehouse.controller.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseStatusDto {

    private String warehouseId;
    private boolean isAvailable;

    public WarehouseStatusDto(String warehouseId, boolean isAvailable) {
        this.warehouseId = warehouseId;
        this.isAvailable = isAvailable;
    }
}