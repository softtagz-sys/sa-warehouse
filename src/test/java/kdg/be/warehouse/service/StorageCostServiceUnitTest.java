package kdg.be.warehouse.service;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import kdg.be.warehouse.domain.warehouse.Warehouse;
import kdg.be.warehouse.domain.warehouse.WarehouseTransaction;
import kdg.be.warehouse.repository.InvoiceRepository;
import kdg.be.warehouse.repository.PricingInfoRepository;
import kdg.be.warehouse.repository.WarehouseTransactionRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest
class StorageCostServiceUnitTest {

    @Autowired
    private StorageCostService storageCostService;

    @MockBean
    private PricingInfoRepository pricingInfoRepository;
    @MockBean
    private WarehouseTransactionRepository warehouseTransactionRepository;
    @MockBean
    private InvoiceRepository invoiceRepository;
    @MockBean
    private WarehouseConfig warehouseConfig;

    @Test
    public void storageCostServiceShouldUseCorrectStorageCostPrice() {

        // Arrange
        Customer customer = new Customer("klant", "antwerpen");
        Material gips = new Material("gips", "NA");
        Warehouse warehouse = new Warehouse(500000, 20000, gips, customer);

        PricingInfo pricing2024 = new PricingInfo(1, LocalDateTime.of(2024, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);
        pricing2024.setValidTo(LocalDateTime.of(2024, 12, 31, 23, 59));
        PricingInfo pricing2025 = new PricingInfo(5, LocalDateTime.of(2025, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);

        WarehouseTransaction trans1 = new WarehouseTransaction(gips, 10000, warehouse, LocalDateTime.of(2024, 2, 1, 1, 1), customer);
        WarehouseTransaction trans2 = new WarehouseTransaction(gips, 10000, warehouse, LocalDateTime.of(2025, 2, 1, 1, 1), customer);


        given(warehouseTransactionRepository.findAllByRemainingAmountGreaterThanAndTransactionTimeBeforeOrderByTransactionTimeAsc(anyDouble(), any(LocalDateTime.class)))
                .willReturn(List.of(trans1, trans2));

        given(pricingInfoRepository.findAllByPriceTypeAndMaterial(PriceType.STORAGE_COST, gips))
                .willReturn(List.of(pricing2024, pricing2025));

        // Act
        storageCostService.addInvoiceStorageCosts();

        ArgumentCaptor<List<Invoice>> invoicesCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(invoiceRepository).saveAll(invoicesCaptor.capture());
        List<Invoice> invoices = invoicesCaptor.getValue();

        // Assert
        assertEquals(1, invoices.size());
        Invoice invoice = invoices.get(0);

        assertEquals(2, invoice.getInvoiceLines().size());
        InvoiceLine invoiceLine1 = invoice.getInvoiceLines().get(0);
        InvoiceLine invoiceLine2 = invoice.getInvoiceLines().get(1);

        assertEquals(1, invoiceLine1.getLineNumber());
        assertEquals(2, invoiceLine2.getLineNumber());
        assertEquals("Storage Cost gips (2024-02-01T01:01)", invoiceLine1.getDescription());
        assertEquals("Storage Cost gips (2025-02-01T01:01)", invoiceLine2.getDescription());
        assertEquals(1f, invoiceLine1.getUnitPrice(), 0.0001);
        assertEquals(5f, invoiceLine2.getUnitPrice(), 0.0001);
        assertEquals(10000f, invoiceLine1.getTotalPrice(), 0.0001);
        assertEquals(50000f, invoiceLine2.getTotalPrice(), 0.0001);
        assertEquals(60000f, invoice.getInvoiceLines().stream().map(InvoiceLine::getTotalPrice).reduce(0f, Float::sum), 0.0001);
    }

    @Test
    public void storageCostServiceShouldNotCreateAnInvoiceWhenNoTransactions() {

        // Arrange
        Customer customer1 = new Customer("klant", "antwerpen");
        Customer customer2 = new Customer("klant", "brussel");
        Material gips = new Material("gips", "NA");
        Warehouse warehouse1 = new Warehouse(500000, 20000, gips, customer1);
        Warehouse warehouse2 = new Warehouse(500000, 10000, gips, customer2);

        PricingInfo pricing2024 = new PricingInfo(1, LocalDateTime.of(2024, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);


        given(warehouseTransactionRepository.findAllByRemainingAmountGreaterThanAndTransactionTimeBeforeOrderByTransactionTimeAsc(anyDouble(), any(LocalDateTime.class)))
                .willReturn(List.of());

        given(pricingInfoRepository.findAllByPriceTypeAndMaterial(PriceType.STORAGE_COST, gips))
                .willReturn(List.of(pricing2024));

        // Act
        storageCostService.addInvoiceStorageCosts();

        ArgumentCaptor<List<Invoice>> invoicesCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(invoiceRepository).saveAll(invoicesCaptor.capture());
        List<Invoice> invoices = invoicesCaptor.getValue();

        // Assert
        assertEquals(0, invoices.size());
    }

    @Test
    public void storageCostServiceShouldCreateAnInvoicePerCustomer() {

        // Arrange
        Customer customer1 = new Customer("klant", "antwerpen");
        Customer customer2 = new Customer("klant", "brussel");
        Material gips = new Material("gips", "NA");
        Warehouse warehouse1 = new Warehouse(500000, 20000, gips, customer1);
        Warehouse warehouse2 = new Warehouse(500000, 10000, gips, customer2);

        PricingInfo pricing2024 = new PricingInfo(1, LocalDateTime.of(2024, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);

        WarehouseTransaction trans1 = new WarehouseTransaction(gips, 10000, warehouse1, LocalDateTime.of(2024, 2, 1, 1, 1), customer1);
        WarehouseTransaction trans2 = new WarehouseTransaction(gips, 10000, warehouse1, LocalDateTime.of(2025, 2, 1, 1, 1), customer1);
        WarehouseTransaction trans3 = new WarehouseTransaction(gips, 10000, warehouse2, LocalDateTime.of(2025, 2, 1, 1, 1), customer2);


        given(warehouseTransactionRepository.findAllByRemainingAmountGreaterThanAndTransactionTimeBeforeOrderByTransactionTimeAsc(anyDouble(), any(LocalDateTime.class)))
                .willReturn(List.of(trans1, trans2, trans3));

        given(pricingInfoRepository.findAllByPriceTypeAndMaterial(PriceType.STORAGE_COST, gips))
                .willReturn(List.of(pricing2024));

        // Act
        storageCostService.addInvoiceStorageCosts();

        ArgumentCaptor<List<Invoice>> invoicesCaptor = ArgumentCaptor.forClass((Class) List.class);
        verify(invoiceRepository).saveAll(invoicesCaptor.capture());
        List<Invoice> invoices = invoicesCaptor.getValue();

        // Assert
        assertEquals(2, invoices.size());
        assertEquals(3, invoices.stream().flatMap(i -> i.getInvoiceLines().stream()).count());
    }

    @Test
    public void storageCostServiceShouldThrowExceptionWhenNoValidCostIsAvailable() {

        // Arrange
        Customer customer = new Customer("klant", "antwerpen");
        Material gips = new Material("gips", "NA");
        Warehouse warehouse = new Warehouse(500000, 20000, gips, customer);

        PricingInfo pricing2024 = new PricingInfo(1, LocalDateTime.of(2024, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);
        pricing2024.setValidTo(LocalDateTime.of(2024, 12, 31, 23, 59));
        PricingInfo pricing2025 = new PricingInfo(5, LocalDateTime.of(2025, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);

        WarehouseTransaction trans1 = new WarehouseTransaction(gips, 10000, warehouse, LocalDateTime.of(2023, 2, 1, 1, 1), customer);
        WarehouseTransaction trans2 = new WarehouseTransaction(gips, 10000, warehouse, LocalDateTime.of(2025, 2, 1, 1, 1), customer);


        given(warehouseTransactionRepository.findAllByRemainingAmountGreaterThanAndTransactionTimeBeforeOrderByTransactionTimeAsc(anyDouble(), any(LocalDateTime.class)))
                .willReturn(List.of(trans1, trans2));

        given(pricingInfoRepository.findAllByPriceTypeAndMaterial(PriceType.STORAGE_COST, gips))
                .willReturn(List.of(pricing2024, pricing2025));

        // Act && Assert
        RuntimeException exception = assertThrows(IllegalStateException.class, () -> storageCostService.addInvoiceStorageCosts());
        assertEquals("No valid storage cost found for material: gips and time: 2023-02-01T01:01", exception.getMessage());
    }

    @Test
    public void storageCostServiceShouldThrowExceptionWhenMultipleValidCostIsAvailable() {

        // Arrange
        Customer customer = new Customer("klant", "antwerpen");
        Material gips = new Material("gips", "NA");
        Warehouse warehouse = new Warehouse(500000, 20000, gips, customer);

        PricingInfo pricing2024 = new PricingInfo(1, LocalDateTime.of(2024, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);
        pricing2024.setValidTo(LocalDateTime.of(2024, 12, 31, 23, 59));
        PricingInfo pricing2024_2 = new PricingInfo(5, LocalDateTime.of(2024, 1, 1, 0, 0), PriceType.STORAGE_COST, gips);

        WarehouseTransaction trans1 = new WarehouseTransaction(gips, 10000, warehouse, LocalDateTime.of(2024, 2, 1, 1, 1), customer);
        WarehouseTransaction trans2 = new WarehouseTransaction(gips, 10000, warehouse, LocalDateTime.of(2025, 2, 1, 1, 1), customer);


        given(warehouseTransactionRepository.findAllByRemainingAmountGreaterThanAndTransactionTimeBeforeOrderByTransactionTimeAsc(anyDouble(), any(LocalDateTime.class)))
                .willReturn(List.of(trans1, trans2));

        given(pricingInfoRepository.findAllByPriceTypeAndMaterial(PriceType.STORAGE_COST, gips))
                .willReturn(List.of(pricing2024, pricing2024_2));

        // Act && Assert
        RuntimeException exception = assertThrows(IllegalStateException.class, () -> storageCostService.addInvoiceStorageCosts());
        assertEquals("Multiple valid storage costs found for material: gips and time: 2024-02-01T01:01", exception.getMessage());
    }


}