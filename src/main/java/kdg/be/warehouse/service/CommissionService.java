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
import kdg.be.warehouse.repository.WarehouseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommissionService {

    private final InvoiceRepository invoiceRepository;
    private final WarehouseConfig warehouseConfig;
    private final MaterialRepository materialRepository;

    public CommissionService(InvoiceRepository invoiceRepository, WarehouseConfig warehouseConfig, MaterialRepository materialRepository) {
        this.invoiceRepository = invoiceRepository;
        this.warehouseConfig = warehouseConfig;
        this.materialRepository = materialRepository;
    }

    public Optional<Invoice> addCommissionInvoice(Customer seller, PurchaseOrder po) {
        Invoice newInvoice = new Invoice(seller);

        for (int i = 0; i < po.getOrderLines().size(); i++) {
            OrderLine orderLine = po.getOrderLines().get(i);

            PricingInfo sellPrice = materialRepository
                    .findByNameIgnoreCaseWithPrices(orderLine.getMaterialName())
                    .map(Material::getPrices)
                    .orElseThrow(() -> new RuntimeException("Unable to find material " + orderLine.getMaterialName()))
                    .stream()
                    .filter(p -> p.getPriceType() == PriceType.SELL_PRICE)
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Sell price not found for material " + orderLine.getMaterialName()));


            double unitPrice = sellPrice.getPrice() * warehouseConfig.getDefaultCommissionOnPOs();

            InvoiceLine newInvoiceLine = new InvoiceLine(
                    i + 1,
                    String.format("Commission %s", orderLine.getMaterialName()),
                    orderLine.getQuantity(),
                    unitPrice,
                    orderLine.getQuantity() * unitPrice,
                    newInvoice
            );

            newInvoice.getInvoiceLines().add(newInvoiceLine);

        }


        return Optional.of(invoiceRepository.save(newInvoice));
    }
}