package kdg.be.warehouse.controller.rabbit;

import kdg.be.warehouse.config.RabbitConfig;
import kdg.be.warehouse.controller.dto.in.DeliveryCompleteDto;
import kdg.be.warehouse.service.WarehouseService;
import kdg.be.warehouse.service.WarehouseTransactionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
public class DeliveryListener {

    private final RabbitConfig rabbitConfig;
    private final WarehouseTransactionService warehouseTransactionService;

    public DeliveryListener(RabbitConfig rabbitConfig, WarehouseTransactionService warehouseTransactionService) {
        this.rabbitConfig = rabbitConfig;
        this.warehouseTransactionService = warehouseTransactionService;
    }

    @RabbitListener(queues = "#{@rabbitConfig.getDeliveryQueue}")
    public void addDelivery(DeliveryCompleteDto deliveryCompleteDto) {
        warehouseTransactionService.addDelivery(
                UUID.fromString(deliveryCompleteDto.getCustomerId()),
                deliveryCompleteDto.getRawMaterial(),
                (float) deliveryCompleteDto.getWeight(),
                deliveryCompleteDto.getTimestamp()
        );
    }


}
