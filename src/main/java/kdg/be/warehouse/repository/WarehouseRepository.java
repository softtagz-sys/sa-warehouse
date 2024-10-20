package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {

    Optional<Warehouse> findByOwner_customerIdAndMaterial_name(UUID customerId, String materialName);
}
