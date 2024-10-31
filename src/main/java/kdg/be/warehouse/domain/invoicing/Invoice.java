package kdg.be.warehouse.domain.invoicing;

import jakarta.persistence.*;
import kdg.be.warehouse.domain.Customer;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID invoiceId;


    private LocalDateTime createdDate;

    @Setter
    private LocalDateTime invoicedDate;

    @Setter
    private LocalDateTime completedDate;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<InvoiceLine> invoiceLines;

    public Invoice() {
    }

    public Invoice(Customer customer) {
        this.createdDate = LocalDateTime.now();
        this.customer = customer;
        this.invoiceLines = new ArrayList<>();
    }

    public Invoice(Customer customer, List<InvoiceLine> invoiceLines) {
        this.customer = customer;
        this.invoiceLines = invoiceLines;
        this.createdDate = LocalDateTime.now();
    }
}
