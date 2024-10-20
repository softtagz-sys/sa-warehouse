package kdg.be.warehouse.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "rabbit")
public class RabbitConfig {

    private String MineralFlowExchange;
    private String DeliveryQueue;
    private String WarehouseDeliveryKey;
}

