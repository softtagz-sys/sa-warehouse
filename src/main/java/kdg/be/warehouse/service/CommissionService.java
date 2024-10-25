package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommissionService {

    private final InvoiceRepository invoiceRepository;

    public CommissionService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public Optional<Invoice> addCommissionInvoice(Customer seller, PurchaseOrder po) {
        Invoice newInvoice = new Invoice(seller);

        for (int i = 0; i < po.getOrderLines().size(); i++) {
            OrderLine orderLine = po.getOrderLines().get(i);

            double unitPrice = 0.01; // TODO calculate

            InvoiceLine newInvoiceLine = new InvoiceLine(
                    i,
                    String.format("Commission %s", orderLine.getMaterialName()),
                    orderLine.getQuantity(),
                    unitPrice,
                    orderLine.getQuantity() * unitPrice
            );

            newInvoice.getInvoiceLines().add(newInvoiceLine);
        }


        return Optional.of(invoiceRepository.save(newInvoice));
    }
}
