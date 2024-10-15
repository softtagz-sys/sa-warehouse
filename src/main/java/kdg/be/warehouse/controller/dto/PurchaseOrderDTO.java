package kdg.be.warehouse.controller.dto;

import java.util.List;
import java.util.UUID;

public class PurchaseOrderDTO {
    private String poNumber;
    private UUID referenceUUID;
    private CustomerDTO buyer;
    private CustomerDTO seller;
    private String vesselNumber;
    private List<OrderLineDTO> orderLines;

    public PurchaseOrderDTO(String poNumber, UUID referenceUUID, CustomerDTO buyer, CustomerDTO seller, String vesselNumber, List<OrderLineDTO> orderLines) {
        this.poNumber = poNumber;
        this.referenceUUID = referenceUUID;
        this.buyer = buyer;
        this.seller = seller;
        this.vesselNumber = vesselNumber;
        this.orderLines = orderLines;
    }
}