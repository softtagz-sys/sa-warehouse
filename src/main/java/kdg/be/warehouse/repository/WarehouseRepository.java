package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByOwnerAndMaterial(Customer owner, Material material);

    Optional<Warehouse> findByOwner_customerIdAndMaterial_nameIgnoreCase(UUID customerId, String materialName);
}
