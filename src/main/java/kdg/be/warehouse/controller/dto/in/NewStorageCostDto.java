package kdg.be.warehouse.controller.dto.in;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NewStorageCostDto {

    @PositiveOrZero
    private float newPrice;

    @FutureOrPresent
    private LocalDateTime validFrom;

    public NewStorageCostDto(float newPrice, LocalDateTime validFrom) {
        this.newPrice = newPrice;
        this.validFrom = validFrom;
    }
}
