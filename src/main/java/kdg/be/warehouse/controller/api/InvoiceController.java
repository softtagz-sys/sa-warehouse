package kdg.be.warehouse.controller.api;

import kdg.be.warehouse.controller.dto.out.InvoiceDto;
import kdg.be.warehouse.controller.dto.out.InvoiceLineDto;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.service.InvoiceService;
import kdg.be.warehouse.utilities.PDFInvoiceGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping("/upcoming/{customerName}")
    public ResponseEntity<List<InvoiceDto>> getUndeliveredInvoices(@PathVariable("customerName") String customerName) {
        try {
            List<Invoice> invoiceList = invoiceService.getOpenCommissionInvoices(customerName);

            List<InvoiceDto> invoiceDtoList = invoiceList
                    .stream()
                    .map(i -> new InvoiceDto(
                            i.getInvoiceId(),
                            i.getCreatedDate(),
                            i.getCustomer().getName(),
                            i.getInvoiceLines().stream().map(il -> new InvoiceLineDto(
                                    il.getLineNumber(),
                                    il.getDescription(),
                                    il.getAmountOfUnits(),
                                    il.getUnitPrice(),
                                    il.getTotalPrice()
                            )).toList(),
                            i.getInvoiceLines().stream().map(InvoiceLine::getTotalPrice).reduce(0f, Float::sum)
                    ))
                    .toList();

            return ResponseEntity.ok(invoiceDtoList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
