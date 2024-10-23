package kdg.be.warehouse.domain.material;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "pricing_infos")
@Getter
public class PricingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID pricingInfoId;

    @Setter
    private float price;

    @Setter
    @NotNull
    private LocalDateTime validFrom;

    @Setter
    private LocalDateTime validTo;

    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    @ManyToOne
    private Material material;

    public PricingInfo() {
    }

    public PricingInfo(float price, LocalDateTime validFrom, PriceType priceType, Material material) {
        this.price = price;
        this.validFrom = validFrom;
        this.validTo = null;
        this.priceType = priceType;
        this.material = material;
    }
}