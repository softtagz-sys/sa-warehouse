package kdg.be.warehouse.domain.material;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "pricing_infos")
@Getter
@Setter
public class PricingInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID pricingInfoId;

    private float price;

    private Date startDate;

    private Date endDate;

    public PricingInfo() {
    }

    public PricingInfo(float price, Date startDate) {
        this(price, startDate, null);
    }

    public PricingInfo(float price, Date startDate, Date endDate) {
        this.price = price;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}