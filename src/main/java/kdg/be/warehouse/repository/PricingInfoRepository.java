package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingInfoRepository extends JpaRepository<PricingInfo, UUID> {

    List<PricingInfo> findAllByPriceTypeAndMaterial(PriceType priceType, Material material);

}
