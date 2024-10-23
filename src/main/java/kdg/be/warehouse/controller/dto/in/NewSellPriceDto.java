package kdg.be.warehouse.controller.dto.in;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;

@Getter
public class NewSellPriceDto {

    @PositiveOrZero
    private float sellPrice;

    public NewSellPriceDto(float sellPrice) {
        this.sellPrice = sellPrice;
    }

    public NewSellPriceDto() {
    }
}
