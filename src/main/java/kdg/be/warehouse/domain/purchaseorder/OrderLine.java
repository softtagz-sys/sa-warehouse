package kdg.be.warehouse.domain.purchaseorder;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "order_lines")
@Getter
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "line_number", nullable = false)
    private int lineNumber;

    @Column(name = "material_type", nullable = false)
    private String materialType;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "uom", nullable = false)
    private String uom;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    public OrderLine() {
    }

    public OrderLine(int lineNumber, String materialType, String description, int quantity, String uom) {
        this.lineNumber = lineNumber;
        this.materialType = materialType;
        this.description = description;
        this.quantity = quantity;
        this.uom = uom;
    }
}