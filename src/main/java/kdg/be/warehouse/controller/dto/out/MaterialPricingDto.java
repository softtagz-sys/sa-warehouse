package kdg.be.warehouse.controller.dto.out;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
public class MaterialPricingDto {

    private UUID materialId;
    private String materialName;
    private PricingInfoDto sellPrice;
    private List<PricingInfoDto> storageCosts;

    public MaterialPricingDto(UUID materialId, String materialName, PricingInfoDto sellPrice, List<PricingInfoDto> storageCosts) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.sellPrice = sellPrice;
        this.storageCosts = storageCosts;
    }
}
