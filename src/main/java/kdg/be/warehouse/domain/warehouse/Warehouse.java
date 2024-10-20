package kdg.be.warehouse.domain.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.material.Material;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "warehouses")
@Getter
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID warehouseId;

    @Setter
    private float maxCapacity;

    @Setter
    private float currentCapacity;

    @ManyToOne
    private Material material;

    @ManyToOne
    private Customer owner;

    public Warehouse() {
    }

    public Warehouse(float maxCapacity, float currentCapacity, Material material, Customer owner) {
        this.maxCapacity = maxCapacity;
        this.currentCapacity = currentCapacity;
        this.material = material;
        this.owner = owner;
    }
}
