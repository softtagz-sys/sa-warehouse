package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.orderLines WHERE po.isCompleted = false")
    List<PurchaseOrder> findOpenPurchaseOrders();

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.orderLines WHERE po.isCompleted = true AND po.completedDate BETWEEN :startDate AND :endDate")
    List<PurchaseOrder> findAllByIsCompletedTrueAndCompletedDateBetween(Date startDate, Date endDate);


}