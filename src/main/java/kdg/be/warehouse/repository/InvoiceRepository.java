package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.invoicing.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}
