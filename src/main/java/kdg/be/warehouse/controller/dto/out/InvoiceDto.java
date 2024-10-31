package kdg.be.warehouse.controller.dto.out;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class InvoiceDto {

    private UUID invoiceId;

    private LocalDateTime createdDate;

    private String customerName;

    private List<InvoiceLineDto> invoiceLines;

    private float totalPrice;

    public InvoiceDto(UUID invoiceId, LocalDateTime createdDate, String customerName, List<InvoiceLineDto> invoiceLines, float totalPrice) {
        this.invoiceId = invoiceId;
        this.createdDate = createdDate;
        this.customerName = customerName;
        this.invoiceLines = invoiceLines;
        this.totalPrice = totalPrice;
    }
}
