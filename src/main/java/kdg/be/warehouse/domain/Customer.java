package kdg.be.warehouse.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID customerId;

    @Column(unique = true)
    @NotBlank
    private String name;

    @NotBlank
    private String address;

    public Customer() {
    }

    public Customer(UUID customerId, String name, String address) {
        this.customerId = customerId;
        this.name = name;
        this.address = address;
    }

    public Customer(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
