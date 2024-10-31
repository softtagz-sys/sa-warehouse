package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.repository.CustomerRepository;
import kdg.be.warehouse.repository.InvoiceRepository;
import kdg.be.warehouse.utilities.PDFInvoiceGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final StorageCostService storageCostService;
    private final PDFInvoiceGenerator pdfInvoiceGenerator;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvoiceService.class);
    private final CustomerRepository customerRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, StorageCostService storageCostService, PDFInvoiceGenerator pdfInvoiceGenerator, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.storageCostService = storageCostService;
        this.pdfInvoiceGenerator = pdfInvoiceGenerator;
        this.customerRepository = customerRepository;
    }


    @Scheduled(cron = "#{@warehouseConfig.getCronStorageCosts()}", zone = "#{@warehouseConfig.getCronTimeZone()}")
    public void generateDailyInvoices() {
        storageCostService.addInvoiceStorageCosts();
        sendOutInvoices();
    }


    public void sendOutInvoices() {
        List<Invoice> invoices = invoiceRepository.findAllUnsentInvoicesByCustomer();

        try {
            List<Invoice> combinedInvoices = invoices.stream()
                    .collect(Collectors.groupingBy(Invoice::getCustomer))
                    .entrySet().stream()
                    .map(e -> combineInvoices(e.getKey(), e.getValue())).toList();

            combinedInvoices.forEach(pdfInvoiceGenerator::generateInvoice);

            invoices.forEach(invoice -> invoice.setInvoicedDate(LocalDateTime.now()));

            invoiceRepository.saveAll(invoices);
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
        }
    }

    public List<Invoice> getOpenCommissionInvoices(String customerName) {
        Customer customer = customerRepository.findByName(customerName)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        List<Invoice> invoices = invoiceRepository.findAllUnsentInvoicesByCustomer(customer);

        return invoices
                .stream()
                .filter(i -> i.getInvoiceLines().stream().anyMatch(il -> il.getDescription().contains("Commission")))
                .toList();
    }

    private Invoice combineInvoices(Customer customer, List<Invoice> invoices) {
        List<InvoiceLine> invoiceLines = invoices.stream().flatMap(i -> i.getInvoiceLines().stream()).toList();
        return new Invoice(customer, invoiceLines );
    }
}
