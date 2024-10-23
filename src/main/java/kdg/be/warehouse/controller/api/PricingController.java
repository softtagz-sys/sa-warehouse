package kdg.be.warehouse.controller.api;

import jakarta.validation.Valid;
import kdg.be.warehouse.controller.dto.in.NewSellPriceDto;
import kdg.be.warehouse.controller.dto.in.NewStorageCostDto;
import kdg.be.warehouse.controller.dto.out.MaterialPricingDto;
import kdg.be.warehouse.controller.dto.out.PricingInfoDto;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import kdg.be.warehouse.service.PricingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @PostMapping("/{materialName}/storageCost")
    public ResponseEntity<MaterialPricingDto> addStorageCost(@PathVariable String materialName, @RequestBody @Valid NewStorageCostDto newStorageCostDto) {
        try {
            Optional<Material> materialOptional = pricingService.changeStorageCost(materialName, newStorageCostDto.getValidFrom(), newStorageCostDto.getNewPrice());
            if (materialOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(this.mapToDto(materialOptional.get()));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).contentType(MediaType.APPLICATION_JSON).build();
            }

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }
    }

    @PatchMapping("/{materialName}/sellPrice")
    public ResponseEntity<MaterialPricingDto> changeSellPrice(@PathVariable String materialName, @RequestBody @Valid NewSellPriceDto newSellPriceDto) {
        try {
             Optional<Material> materialOptional = pricingService.changeSellPrice(materialName, newSellPriceDto.getSellPrice());
             if (materialOptional.isPresent()) {
                return ResponseEntity.ok().body(this.mapToDto(materialOptional.get()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }
    }

    private MaterialPricingDto mapToDto(Material material) {
        return new MaterialPricingDto(
                material.getMaterialId(),
                material.getName(),
                material.getPrices().stream()
                        .filter(m -> m.getPriceType() == PriceType.SELL_PRICE)
                        .map(this::mapToDto)
                        .toList().getFirst(),
                material.getPrices().stream()
                        .filter(m -> m.getPriceType() == PriceType.STORAGE_COST)
                        .map(this::mapToDto)
                        .toList()
        );
    }

    private PricingInfoDto mapToDto(PricingInfo pricingInfo) {
        return new PricingInfoDto(
                pricingInfo.getPricingInfoId(),
                pricingInfo.getPrice(),
                pricingInfo.getValidFrom(),
                pricingInfo.getValidTo()
        );
    }
}
