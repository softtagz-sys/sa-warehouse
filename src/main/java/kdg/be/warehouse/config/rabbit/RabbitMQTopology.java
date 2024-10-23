package kdg.be.warehouse.config.rabbit;

import kdg.be.warehouse.config.RabbitConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopology {

    private final RabbitConfig rabbitConfig;

    public RabbitMQTopology(RabbitConfig rabbitConfig) {
        this.rabbitConfig = rabbitConfig;
    }

    @Bean
    public TopicExchange mineralFlowExchange() { return new TopicExchange(rabbitConfig.getMineralFlowExchange());}

    @Bean
    public Queue topicQueueDelivery() { return new Queue(rabbitConfig.getDeliveryQueue());}

    @Bean
    public Binding topicDeliveryBinding(TopicExchange mineralFlowExchange, Queue topicQueueDelivery) {
        return BindingBuilder.bind(topicQueueDelivery).to(mineralFlowExchange).with(rabbitConfig.getWarehouseDeliveryKey());
    }
}
