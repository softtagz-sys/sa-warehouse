package kdg.be.warehouse.controller.dto.mapper;

import kdg.be.warehouse.controller.dto.out.MaterialPricingDto;
import kdg.be.warehouse.controller.dto.out.PricingInfoDto;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import javax.xml.transform.Source;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialPricingMapper {
    
    @Mapping(source = "name", target = "materialName")
    @Mapping(source = "prices", target = "sellPrice", qualifiedByName = "mapSellPrice")
    @Mapping(source = "prices", target = "storageCosts", qualifiedByName = "mapStorageCosts")
    MaterialPricingDto materialPricingDto(Material material);
    
    PricingInfoDto pricingInfoDto(PricingInfo pricingInfo);
    
    @Named("mapSellPrice")
    default PricingInfoDto mapSellPrice(List<PricingInfo> prices) {
        return prices.stream()
                .filter(price -> price.getPriceType() == PriceType.SELL_PRICE)
                .findFirst()
                .map(this::pricingInfoDto)
                .orElse(null);
    }

    // Custom mapping for the list of storage costs (filtering for STORAGE_COST)
    @Named("mapStorageCosts")
    default List<PricingInfoDto> mapStorageCosts(List<PricingInfo> prices) {
        return prices.stream()
                .filter(price -> price.getPriceType() == PriceType.STORAGE_COST)
                .map(this::pricingInfoDto)
                .toList();
    }
    
}
