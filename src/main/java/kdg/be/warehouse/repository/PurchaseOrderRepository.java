package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByPoNumberAndSeller_CustomerId(String poNumber, UUID sellerId);

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.orderLines WHERE po.isCompleted = false")
    List<PurchaseOrder> findOpenPurchaseOrders();

    @Query("SELECT po FROM PurchaseOrder po LEFT JOIN FETCH po.orderLines WHERE po.isCompleted = true AND po.completedDate BETWEEN :startDate AND :endDate")
    List<PurchaseOrder> findAllByIsCompletedTrueAndCompletedDateBetween(Date startDate, Date endDate);

    Optional<PurchaseOrder> findByPoNumber(String poNumber);

    @Query("SELECT ol FROM OrderLine ol WHERE ol.purchaseOrder.id = :purchaseOrderId")
    List<OrderLine> findOrderLinesByPurchaseOrderId(@Param("purchaseOrderId") UUID purchaseOrderId);
}