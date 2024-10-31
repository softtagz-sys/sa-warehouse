package kdg.be.warehouse.service;

import kdg.be.warehouse.config.WarehouseConfig;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import kdg.be.warehouse.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StorageCostService {

    private final PricingInfoRepository pricingInfoRepository;
    private final WarehouseTransactionRepository warehouseTransactionRepository;
    private final InvoiceRepository invoiceRepository;
    private final WarehouseConfig warehouseConfig;

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCostService.class);

    public StorageCostService(PricingInfoRepository pricingInfoRepository, WarehouseTransactionRepository warehouseTransactionRepository, InvoiceRepository invoiceRepository, WarehouseConfig warehouseConfig) {
        this.pricingInfoRepository = pricingInfoRepository;
        this.warehouseTransactionRepository = warehouseTransactionRepository;
        this.invoiceRepository = invoiceRepository;
        this.warehouseConfig = warehouseConfig;
    }

    public void addInvoiceStorageCosts() {

        LocalDateTime dt = LocalDateTime.now().minusDays(1);

        List<Invoice> createdInvoices =
                warehouseTransactionRepository.findAllByRemainingAmountGreaterThanAndTransactionTimeBeforeOrderByTransactionTimeAsc(0, dt)
                        .stream()
                        .collect(Collectors.groupingBy(WarehouseTransaction::getCustomer))
                        .entrySet().stream()
                        .map(e -> calculateInvoice(e.getKey(), e.getValue()))
                        .toList();

        invoiceRepository.saveAll(createdInvoices);

        LOGGER.info("{} invoices created", createdInvoices.size());
    }

    private Invoice calculateInvoice(Customer customer, List<WarehouseTransaction> transactions) {
        Invoice invoice = new Invoice(customer);

        for (int i = 0; i < transactions.size(); i++) {
            WarehouseTransaction transaction = transactions.get(i);
            float unitPrice = calculateStorageCostUnitPrice(transaction);

            float totalPrice = Math.round(unitPrice * transaction.getRemainingAmount() * 100.0) / 100.0f;

            InvoiceLine invoiceLine = new InvoiceLine(
                    i + 1,
                    String.format("Storage Cost %s (%s)", transaction.getMaterial().getName(), transaction.getTransactionTime().toString()),
                    transaction.getRemainingAmount(),
                    unitPrice,
                    totalPrice,
                    invoice
            );

            invoice.getInvoiceLines().add(invoiceLine);
        }
        return invoice;
    }


    private float calculateStorageCostUnitPrice(WarehouseTransaction transaction) {
        PricingInfo priceInfo = pricingInfoRepository.findAllByPriceTypeAndMaterial(PriceType.STORAGE_COST, transaction.getMaterial())
                .stream()
                .filter(p -> p.getValidFrom().isBefore(transaction.getTransactionTime()))
                .filter(p -> p.getValidTo() == null || p.getValidTo().isAfter(transaction.getTransactionTime()))
                .reduce((a, b) -> {
                    throw new IllegalStateException("Multiple valid storage costs found for material: " + transaction.getMaterial().getName() + " and time: " + transaction.getTransactionTime().toString());
                })
                .orElseThrow(() -> new IllegalStateException("No valid storage cost found for material: " + transaction.getMaterial().getName() + " and time: " + transaction.getTransactionTime().toString()));

        return priceInfo.getPrice();
    }
}
