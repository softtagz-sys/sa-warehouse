package kdg.be.warehouse.controller.api;

import kdg.be.warehouse.controller.dto.mapper.InvoiceMapper;
import kdg.be.warehouse.controller.dto.out.InvoiceDto;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.service.InvoiceService;
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
    private final InvoiceMapper invoiceMapper;

    public InvoiceController(InvoiceService invoiceService, InvoiceMapper invoiceMapper) {
        this.invoiceService = invoiceService;
        this.invoiceMapper = invoiceMapper;
    }

    @GetMapping("/upcoming/{customerName}")
    public ResponseEntity<List<InvoiceDto>> getUndeliveredInvoices(@PathVariable("customerName") String customerName) {
        try {
            List<Invoice> invoiceList = invoiceService.getOpenCommissionInvoices(customerName);

            List<InvoiceDto> invoiceDtoList = invoiceList
                    .stream()
                    .map(invoiceMapper::invoiceDto)
                    .toList();

            return ResponseEntity.ok(invoiceDtoList);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
