package kdg.be.warehouse.controller.dto.out;

import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PricingInfoDto {

    private UUID pricingInfoId;
    private float price;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    public PricingInfoDto(UUID pricingInfoId, float price, LocalDateTime validFrom, LocalDateTime validTo) {
        this.pricingInfoId = pricingInfoId;
        this.price = price;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

}
