package kdg.be.warehouse.domain.material;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "materials")
@Getter
@Setter
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID materialId;

    private String name;

    @OneToOne
    private PricingInfo storageCost;

    @OneToOne
    private PricingInfo salesPrice;

    private String description;

    public Material() {
    }

    public Material(String name, PricingInfo storageCost, PricingInfo salesPrice, String description) {
        this.name = name;
        this.storageCost = storageCost;
        this.salesPrice = salesPrice;
        this.description = description;
    }
}