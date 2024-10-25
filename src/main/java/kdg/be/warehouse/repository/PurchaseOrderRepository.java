package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    @Query(
            """
               SELECT po
               FROM PurchaseOrder po
               JOIN FETCH po.orderLines
               WHERE po.seller.customerId = :customerId
               AND po.poNumber = :poNumber
            """
    )
    Optional<PurchaseOrder> findByPoNumberAndSeller_customerId(String poNumber, UUID customerId);
}