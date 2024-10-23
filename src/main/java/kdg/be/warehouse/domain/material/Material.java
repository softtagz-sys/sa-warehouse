package kdg.be.warehouse.domain.material;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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

    @OneToMany(mappedBy = "material")
    private List<PricingInfo> prices;

    @NotBlank
    @Column(columnDefinition = "TEXT") // Necessary for varchar > 255
    private String description;

    public Material() {
    }

    public Material(String name, String description) {
        this.name = name;
        this.description = description;
    }
}