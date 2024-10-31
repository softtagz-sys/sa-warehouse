package kdg.be.warehouse.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class OrderLineDTO {
    
    @Positive
    private int lineNumber;
    
    @NotBlank
    private String materialName;
    
    @Positive
    private float quantity;
    
    @NotBlank
    private String uom;

    public OrderLineDTO(int lineNumber, String materialName, float quantity, String uom) {
        this.lineNumber = lineNumber;
        this.materialName = materialName;
        this.quantity = quantity;
        this.uom = uom;
    }
}