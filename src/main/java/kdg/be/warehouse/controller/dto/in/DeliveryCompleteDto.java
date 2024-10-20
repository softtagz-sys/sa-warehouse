package kdg.be.warehouse.controller.dto.in;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DeliveryCompleteDto {

    private final String customerId;
    private final String rawMaterial;
    private final double weight;
    private final LocalDateTime timestamp;

    public DeliveryCompleteDto(String customerId, String rawMaterial, double weight, LocalDateTime timestamp) {
        this.customerId = customerId;
        this.rawMaterial = rawMaterial;
        this.weight = weight;
        this.timestamp = timestamp;
    }
}