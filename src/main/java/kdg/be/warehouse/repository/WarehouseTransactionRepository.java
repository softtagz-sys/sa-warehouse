package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface WarehouseTransactionRepository extends JpaRepository<WarehouseTransaction, UUID> {


    @Query(
            """
            SELECT sum(wt.amount)
            From WarehouseTransaction wt
            WHERE wt.warehouse.warehouseId = :warehouseId
            """
    )
    float sumOfAmount(UUID warehouseId);
}
