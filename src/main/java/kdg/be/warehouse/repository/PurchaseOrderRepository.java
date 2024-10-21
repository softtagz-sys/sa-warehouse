package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByPoNumberAndSeller_customerId(String poNumber, UUID customerId);
}