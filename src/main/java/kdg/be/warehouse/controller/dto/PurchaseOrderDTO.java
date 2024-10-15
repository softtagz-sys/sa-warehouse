package kdg.be.warehouse.controller.dto;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class PurchaseOrderDTO {
    private String poNumber;
    private String referenceUUID;
    private CustomerDTO customerParty;
    private CustomerDTO sellerParty;
    private String vesselNumber;
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