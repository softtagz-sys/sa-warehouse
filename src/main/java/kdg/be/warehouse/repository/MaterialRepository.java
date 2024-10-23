package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {
    Optional<Material> findByNameIgnoreCase(String name);


    @Query(
            """
            SELECT m
            FROM Material m
            JOIN FETCH m.prices p
            WHERE lower(m.name) = lower(:name)
            """
    )
    Optional<Material> findByNameIgnoreCaseWithPrices(String name);




}
