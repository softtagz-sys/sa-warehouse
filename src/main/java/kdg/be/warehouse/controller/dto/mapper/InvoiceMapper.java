package kdg.be.warehouse.controller.dto.mapper;

import kdg.be.warehouse.controller.dto.out.InvoiceDto;
import kdg.be.warehouse.controller.dto.out.InvoiceLineDto;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;


@Mapper(componentModel = "spring")
public interface InvoiceMapper {

    @Mapping(source = "customer.name", target = "customerName")
    @Mapping(source = "invoiceLines", target = "totalPrice", qualifiedByName = "calculateTotalPrice")
    InvoiceDto invoiceDto(Invoice invoice);

    InvoiceLineDto invoiceLineDto(InvoiceLine invoiceLine);

    @Named("calculateTotalPrice")
    default float calculateTotalPrice(List<InvoiceLine> invoiceLines) {
        return invoiceLines.stream()
                .map(InvoiceLine::getTotalPrice)
                .reduce(0f, Float::sum);
    }
}
