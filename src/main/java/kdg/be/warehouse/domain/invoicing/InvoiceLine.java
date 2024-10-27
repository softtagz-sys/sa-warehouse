package kdg.be.warehouse.domain.invoicing;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID invoiceLineId;

    @Positive
    private int lineNumber;

    private String description;

    private double amountOfUnits;
    private double unitPrice;
    private double totalPrice;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    public InvoiceLine() {
    }

    public InvoiceLine(int lineNumber, String description, double amountOfUnits, double unitPrice, double totalPrice, Invoice invoice) {
        this.lineNumber = lineNumber;
        this.description = description;
        this.amountOfUnits = amountOfUnits;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.invoice = invoice;
    }
}
