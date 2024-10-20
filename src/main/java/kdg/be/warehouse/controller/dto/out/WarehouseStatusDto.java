package kdg.be.warehouse.controller.dto.out;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WarehouseStatusDto {

    private boolean isAvailable;

    public WarehouseStatusDto(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}