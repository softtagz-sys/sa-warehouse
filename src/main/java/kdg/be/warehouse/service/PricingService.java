package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import kdg.be.warehouse.repository.MaterialRepository;
import kdg.be.warehouse.repository.PricingInfoRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PricingService {

    private static final Logger logger = LogManager.getLogger(PricingService.class);

    private final MaterialRepository materialRepository;
    private final PricingInfoRepository pricingInfoRepository;


    public PricingService(MaterialRepository materialRepository, PricingInfoRepository pricingInfoRepository) {
        this.materialRepository = materialRepository;
        this.pricingInfoRepository = pricingInfoRepository;
    }


    @Transactional
    public Optional<Material> changeStorageCost(String materialName, LocalDateTime validFrom, float newPrice) {
        Material material = materialRepository.findByNameIgnoreCaseWithPrices(materialName)
                .orElseThrow(() -> {
                    logger.error("No material found with name: {}", materialName);
                    return new RuntimeException(String.format("No material with %s found.", materialName));
                });


        PricingInfo latestCost = material.getPrices().stream()
                .filter(price -> price.getPriceType() == PriceType.STORAGE_COST)
                .filter(s -> s.getValidTo() == null).findAny()
                .orElseThrow(() -> {
                    logger.error("No or multiple storage costs found for {}", materialName);
                    return new RuntimeException("No or multiple Storage Costs Found");
                });

        if (validFrom.isBefore(latestCost.getValidFrom())) {
            logger.error("User attempted to update with invalid storage cost date: {}", validFrom);
           return Optional.empty();
        }

        latestCost.setValidTo(validFrom);
        pricingInfoRepository.save(latestCost);

        PricingInfo newPricingInfo = new PricingInfo(newPrice, validFrom, PriceType.STORAGE_COST, material);
        pricingInfoRepository.save(newPricingInfo);

        material.getPrices().add(newPricingInfo);
        return Optional.of(material);

    }

    public Optional<Material> changeSellPrice(String materialName, float newPrice) {
        Material material = materialRepository.findByNameIgnoreCaseWithPrices(materialName)
                .orElseThrow(() -> {
                    logger.error("No material found with name: {}", materialName);
                    return new RuntimeException(String.format("No material with %s found.", materialName));
                });


        PricingInfo sellPrice = material.getPrices().stream()
                .filter(p -> p.getPriceType() == PriceType.SELL_PRICE)
                .reduce((first, second) -> {
                    logger.error("Multiple sell prices found for {}", materialName);
                    throw  new RuntimeException("Multiple sell prices Found");
                })
                .orElseThrow(() -> {
                    logger.error("No sell prices found for {}", materialName);
                    return new RuntimeException("No sell prices found");
                });

        sellPrice.setValidFrom(LocalDateTime.now());
        sellPrice.setPrice(newPrice);
        pricingInfoRepository.save(sellPrice);

        return Optional.of(material);
    }


}
