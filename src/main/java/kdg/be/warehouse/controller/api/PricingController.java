package kdg.be.warehouse.controller.api;

import jakarta.validation.Valid;
import kdg.be.warehouse.controller.dto.in.NewSellPriceDto;
import kdg.be.warehouse.controller.dto.in.NewStorageCostDto;
import kdg.be.warehouse.controller.dto.mapper.MaterialPricingMapper;
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
    private final MaterialPricingMapper materialPricingMapper;

    public PricingController(PricingService pricingService, MaterialPricingMapper materialPricingMapper) {
        this.pricingService = pricingService;
        this.materialPricingMapper = materialPricingMapper;
    }

    @PostMapping("/{materialName}/storageCost")
    public ResponseEntity<MaterialPricingDto> addStorageCost(@PathVariable String materialName, @RequestBody @Valid NewStorageCostDto newStorageCostDto) {
        try {
            Optional<Material> materialOptional = pricingService.changeStorageCost(materialName, newStorageCostDto.getValidFrom(), newStorageCostDto.getNewPrice());
            if (materialOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(materialPricingMapper.materialPricingDto(materialOptional.get()));
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
                return ResponseEntity.ok().body(materialPricingMapper.materialPricingDto(materialOptional.get()));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.APPLICATION_JSON).build();
        }
    }
}
