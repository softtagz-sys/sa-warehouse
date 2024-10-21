package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import kdg.be.warehouse.domain.material.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WarehouseTransactionRepository extends JpaRepository<WarehouseTransaction, UUID> {


    List<WarehouseTransaction> findByWarehouseAndMaterialOrderByTransactionTimeAsc(Warehouse warehouse, Material material);

    @Query(
            """
            SELECT sum(wt.remainingAmount)
            From WarehouseTransaction wt
            WHERE wt.warehouse.warehouseId = :warehouseId
            """
    )
    float sumOfAmount(UUID warehouseId);

}
