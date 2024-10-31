package kdg.be.warehouse.repository;

import kdg.be.warehouse.domain.Customer;
import kdg.be.warehouse.domain.invoicing.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {


    @Query(value =
            """
            SELECT I
            FROM Invoice I
            JOIN FETCH I.invoiceLines LI
            WHERE I.invoicedDate IS NULL
            """)
    List<Invoice> findAllUnsentInvoicesByCustomer();



    @Query (value =
            """
            SELECT I
            FROM Invoice I
            JOIN FETCH I.invoiceLines LI
            WHERE I.invoicedDate IS NULL
            AND I.customer = :customer
            """)
    List<Invoice> findAllUnsentInvoicesByCustomer(Customer customer) ;
}
