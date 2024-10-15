package kdg.be.warehouse.domain.warehouse;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import kdg.be.warehouse.domain.Customer;
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

    @NotBlank
    private String location;

    @Setter
    private float capacity;

    @OneToOne
    private Customer owner;

    public Warehouse() {
    }

    public Warehouse(String location, float capacity, Customer owner) {
        this.location = location;
        this.capacity = capacity;
        this.owner = owner;
    }
}
