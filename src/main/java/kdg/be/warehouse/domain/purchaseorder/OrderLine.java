package kdg.be.warehouse.domain.purchaseorder;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "order_lines")
@Getter
@Setter
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "material_name", nullable = false)
    private String materialName;

    @Column(name = "description")
    private String description;
    
    @Column(name = "quantity", nullable = false)
    private float quantity;

    @Column(name = "uom", nullable = false)
    private String uom;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    public OrderLine() {
    }

    public OrderLine(int lineNumber, String materialName, String description, float quantity, String uom) {
        this.lineNumber = lineNumber;
        this.materialName = materialName;
        this.description = description;
        this.quantity = quantity;
        this.uom = uom;
    }
}