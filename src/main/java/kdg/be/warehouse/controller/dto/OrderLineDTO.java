package kdg.be.warehouse.controller.dto;

import lombok.Getter;

@Getter
public class OrderLineDTO {
    private int lineNumber;
    private String materialName;
    private int quantity;
    private String uom;

    public OrderLineDTO(int lineNumber, String materialName, int quantity, String uom) {
        this.lineNumber = lineNumber;
        this.materialName = materialName;
        this.quantity = quantity;
        this.uom = uom;
    }
}