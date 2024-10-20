package kdg.be.warehouse.controller.rabbit;

import kdg.be.warehouse.config.RabbitConfig;
import kdg.be.warehouse.controller.dto.in.DeliveryCompleteDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class DeliveryListener {

    private final RabbitConfig rabbitConfig;

    public DeliveryListener(RabbitConfig rabbitConfig) {
        this.rabbitConfig = rabbitConfig;
    }

    @RabbitListener(queues = "#{@rabbitConfig.getDeliveryQueue}")
    public void addDelivery(DeliveryCompleteDto deliveryCompleteDto) {

        // TODO Add Business Logic
        System.out.println(deliveryCompleteDto.getCustomerId());
    }


}
