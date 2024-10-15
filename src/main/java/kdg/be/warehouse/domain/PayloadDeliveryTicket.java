package kdg.be.warehouse.domain;

import kdg.be.warehouse.domain.material.Material;
import kdg.be.warehouse.domain.warehouse.Warehouse;

import java.time.LocalDateTime;
import java.util.UUID;

public class PayloadDeliveryTicket {
    private UUID payloadDeliveryTicketId;
    private Material material;
    private LocalDateTime deliveryTime;
    private Warehouse warehouse;
}
