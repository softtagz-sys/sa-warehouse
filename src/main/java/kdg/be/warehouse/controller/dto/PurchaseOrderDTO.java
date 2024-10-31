package kdg.be.warehouse.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class PurchaseOrderDTO {
    
    @NotBlank
    private String poNumber;
    @NotBlank
    private String referenceUUID;
    @NotNull
    private CustomerDTO customerParty;
    @NotNull
    private CustomerDTO sellerParty;
    @NotNull
    private String vesselNumber;
    @NotNull
    private List<OrderLineDTO> orderLines;

    public PurchaseOrderDTO() {
    }

    public PurchaseOrderDTO(String poNumber, String referenceUUID, CustomerDTO customerParty, CustomerDTO sellerParty, String vesselNumber, List<OrderLineDTO> orderLines) {
        this.poNumber = poNumber;
        this.referenceUUID = referenceUUID;
        this.customerParty = customerParty;
        this.sellerParty = sellerParty;
        this.vesselNumber = vesselNumber;
        this.orderLines = orderLines;
    }
}