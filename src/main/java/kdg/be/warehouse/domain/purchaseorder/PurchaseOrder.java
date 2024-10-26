package kdg.be.warehouse.domain.purchaseorder;

import jakarta.persistence.*;
import kdg.be.warehouse.domain.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "po_number", nullable = false)
    private String poNumber;

    @Column(name = "reference_uuid", nullable = false)
    private UUID referenceUUID;

    @ManyToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Customer buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Customer seller;

    @Column(name = "vessel_number")
    private String vesselNumber;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLine> orderLines;

    public PurchaseOrder() {
    }

    public PurchaseOrder(String poNumber, UUID referenceUUID, Customer buyer, Customer seller, String vesselNumber, List<OrderLine> orderLines) {
        this.poNumber = poNumber;
        this.referenceUUID = referenceUUID;
        this.buyer = buyer;
        this.seller = seller;
        this.vesselNumber = vesselNumber;
        this.orderLines = orderLines;
    }
}