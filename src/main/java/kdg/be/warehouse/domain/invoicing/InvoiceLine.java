package kdg.be.warehouse.domain.invoicing;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.UUID;

@Getter
@Entity
@Table(name = "invoice_lines")
public class InvoiceLine {

    @Id
    private UUID invoiceLineId;

    @Positive
    private int lineNumber;

    private String description;

    private double amountOfUnits;
    private double unitPrice;
    private double totalPrice;

    public InvoiceLine() {
    }

    public InvoiceLine(int lineNumber, String description, double amountOfUnits, double unitPrice, double totalPrice) {
        this.lineNumber = lineNumber;
        this.description = description;
        this.amountOfUnits = amountOfUnits;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}
