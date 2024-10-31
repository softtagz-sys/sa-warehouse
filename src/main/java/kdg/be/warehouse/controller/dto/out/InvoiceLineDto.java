package kdg.be.warehouse.controller.dto.out;

import lombok.Getter;

@Getter
public class InvoiceLineDto {

    private int lineNumber;
    private String description;
    private float amountOfUnits;
    private float unitPrice;
    private float totalPrice;

    public InvoiceLineDto(int lineNumber, String description, float amountOfUnits, float unitPrice, float totalPrice) {
        this.lineNumber = lineNumber;
        this.description = description;
        this.amountOfUnits = amountOfUnits;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
    }
}
