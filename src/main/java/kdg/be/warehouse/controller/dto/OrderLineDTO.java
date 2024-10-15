package kdg.be.warehouse.controller.dto;

public class OrderLineDTO {
    private int lineNumber;
    private String materialType;
    private String description;
    private int quantity;
    private String uom;

    public OrderLineDTO(int lineNumber, String materialType, String description, int quantity, String uom) {
        this.lineNumber = lineNumber;
        this.materialType = materialType;
        this.description = description;
        this.quantity = quantity;
        this.uom = uom;
    }
}