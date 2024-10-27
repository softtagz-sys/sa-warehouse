package kdg.be.warehouse.service;

import kdg.be.warehouse.config.WarehouseConfig;
import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.invoicing.Invoice;
import kdg.be.warehouse.domain.invoicing.InvoiceLine;
import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.material.PriceType;
import kdg.be.warehouse.domain.material.PricingInfo;
import kdg.be.warehouse.domain.purchaseorder.OrderLine;
import kdg.be.warehouse.domain.purchaseorder.PurchaseOrder;
import kdg.be.warehouse.repository.InvoiceRepository;
import kdg.be.warehouse.repository.MaterialRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class CommissionServiceUnitTest {

    @Autowired
    private CommissionService commissionService;

    @MockBean
    private MaterialRepository materialRepository;
    @MockBean
    private WarehouseConfig warehouseConfig;
    @MockBean
    private InvoiceRepository invoiceRepository;


    @Test
    public void commissionCalculatorShouldUseCorrectCommissionPercentage() {
        // Arrange
        Customer seller = new Customer("verkoper", "antwerpen");
        Customer buyer = new Customer("koper", "antwerpen");
        OrderLine orderLine = new OrderLine(1, "Petcoke", "NA", 100, "t");
        PurchaseOrder po = new PurchaseOrder("TEST", UUID.randomUUID(), buyer, seller, "boot", Arrays.asList(orderLine));


        Material material = new Material("Petcoke", "NA");
        PricingInfo sellPrice = new PricingInfo(100, LocalDateTime.of(2024, 1, 1, 1, 1), PriceType.SELL_PRICE, material);
        material.setPrices(List.of(sellPrice));


        given(materialRepository.findByNameIgnoreCaseWithPrices("Petcoke")).willReturn(Optional.of(material));
        given(warehouseConfig.getDefaultCommissionOnPOs()).willReturn(0.01);
        given(invoiceRepository.save(any(Invoice.class))).willReturn(new Invoice());

        // Act
        commissionService.addCommissionInvoice(seller, po);
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());
        Invoice capturedInvoice = invoiceCaptor.getValue();

        // Assert

        assertEquals(1, capturedInvoice.getInvoiceLines().size());

        InvoiceLine invoiceLine = capturedInvoice.getInvoiceLines().get(0);
        assertEquals(1, invoiceLine.getLineNumber());
        assertEquals("Commission Petcoke", invoiceLine.getDescription());
        assertEquals(100, invoiceLine.getAmountOfUnits());
        assertEquals(1, invoiceLine.getUnitPrice());
        assertEquals(100, invoiceLine.getTotalPrice());
        assertEquals(capturedInvoice, invoiceLine.getInvoice()); // Ensure the invoice object is correctly set
    }

      @Test
    public void commissionCalculatorShouldCalculateMultipleOrderLines() {
        // Arrange
        Customer seller = new Customer("verkoper", "antwerpen");
        Customer buyer = new Customer("koper", "antwerpen");
        OrderLine orderLine = new OrderLine(1, "Petcoke", "NA", 100, "t");
        OrderLine orderLine2 = new OrderLine(2, "Gips", "NA", 100, "t");

        PurchaseOrder po = new PurchaseOrder("TEST", UUID.randomUUID(), buyer, seller, "boot", Arrays.asList(orderLine, orderLine2));

        Material material = new Material("Petcoke", "NA");
        Material material2 = new Material("Gips", "NA");
        PricingInfo sellPrice = new PricingInfo(100, LocalDateTime.of(2024, 1, 1, 1, 1), PriceType.SELL_PRICE, material);
        PricingInfo sellPrice2 = new PricingInfo(10, LocalDateTime.of(2024, 1, 1, 1, 1), PriceType.SELL_PRICE, material2);
        material.setPrices(List.of(sellPrice));
        material2.setPrices(List.of(sellPrice2));


        given(materialRepository.findByNameIgnoreCaseWithPrices("Petcoke")).willReturn(Optional.of(material));
        given(materialRepository.findByNameIgnoreCaseWithPrices("Gips")).willReturn(Optional.of(material2));

        given(warehouseConfig.getDefaultCommissionOnPOs()).willReturn(0.01);
        given(invoiceRepository.save(any(Invoice.class))).willReturn(new Invoice());

        // Act
        commissionService.addCommissionInvoice(seller, po);
        ArgumentCaptor<Invoice> invoiceCaptor = ArgumentCaptor.forClass(Invoice.class);
        verify(invoiceRepository).save(invoiceCaptor.capture());
        Invoice capturedInvoice = invoiceCaptor.getValue();

        // Assert
        assertEquals(2, capturedInvoice.getInvoiceLines().size());
        assertEquals(110, capturedInvoice.getInvoiceLines().stream().map(InvoiceLine::getTotalPrice).reduce(0.0, Double::sum));
    }



    @Test
    public void commissionCalculatorShouldThrowExceptionWhenNoValidMaterial() {
        // Arrange
        Customer seller = new Customer("verkoper", "antwerpen");
        Customer buyer = new Customer("koper", "antwerpen");
        OrderLine orderLine = new OrderLine(1, "NonExistentMaterial", "NA", 100, "t");
        PurchaseOrder po = new PurchaseOrder("TEST", UUID.randomUUID(), buyer, seller, "boot", Arrays.asList(orderLine));


        given(materialRepository.findByNameIgnoreCaseWithPrices("NonExistentMaterial")).willReturn(Optional.empty());
        given(warehouseConfig.getDefaultCommissionOnPOs()).willReturn(0.01);
        given(invoiceRepository.save(any(Invoice.class))).willReturn(new Invoice());

        // Act && Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commissionService.addCommissionInvoice(seller, po));
        assertEquals("Unable to find material NonExistentMaterial", exception.getMessage());

    }

    @Test
    public void commissionCalculatorShouldThrowExceptionWhenNoSellPriceIsAvailable() {
        /// Arrange
        Customer seller = new Customer("verkoper", "antwerpen");
        Customer buyer = new Customer("koper", "antwerpen");
        OrderLine orderLine = new OrderLine(1, "Petcoke", "NA", 100, "t");
        PurchaseOrder po = new PurchaseOrder("TEST", UUID.randomUUID(), buyer, seller, "boot", Arrays.asList(orderLine));


        Material material = new Material("Petcoke", "NA");
        material.setPrices(List.of());


        given(materialRepository.findByNameIgnoreCaseWithPrices("Petcoke")).willReturn(Optional.of(material));
        given(warehouseConfig.getDefaultCommissionOnPOs()).willReturn(0.01);
        given(invoiceRepository.save(any(Invoice.class))).willReturn(new Invoice());

        // Act && Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> commissionService.addCommissionInvoice(seller, po));
        assertEquals("Sell price not found for material Petcoke", exception.getMessage());
    }
}