package kdg.be.warehouse.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID customerId;

    @Column(unique = true)
    @NotBlank(message = "A customer must have a name")
    private String name;

    @NotBlank(message = "A customer must have an address")
    private String address;

    public Customer() {
    }

    public Customer(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
