package kdg.be.warehouse.domain.warehouse;

import jakarta.persistence.*;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "warehouse_transactions")
@Getter
@Setter
public class WarehouseTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID warehouseTransactionId;

    @ManyToOne
    private Material material;

    private float remainingAmount;
    private float initialAmount;

    @ManyToOne
    private Warehouse warehouse;

    private LocalDateTime transactionTime;

    @ManyToOne
    private Customer customer;

    public WarehouseTransaction() {
    }

    public WarehouseTransaction(Material material, float initialAmount, Warehouse warehouse, Customer customer) {
        this.material = material;
        this.initialAmount = initialAmount;
        this.remainingAmount = initialAmount;
        this.warehouse = warehouse;
        this.transactionTime = LocalDateTime.now();
        this.customer = customer;
    }
}