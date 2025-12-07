package com.keki.orderservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "keki.exchange";

    public static final String QUEUE_ORDER_PLACED = "inventory.order-placed";
    public static final String QUEUE_INVENTORY_RESULT = "order.inventory-result";

    @Bean
    public TopicExchange kekiExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue inventoryOrderPlacedQueue() {
        return new Queue(QUEUE_ORDER_PLACED, true);
    }

    @Bean
    public Queue orderInventoryResultQueue() {
        return new Queue(QUEUE_INVENTORY_RESULT, true);
    }

    @Bean
    public Binding bindOrderPlaced(Queue inventoryOrderPlacedQueue, TopicExchange kekiExchange) {
        return BindingBuilder.bind(inventoryOrderPlacedQueue)
                .to(kekiExchange)
                .with("order.placed");
    }

    @Bean
    public Binding bindInventoryResult(Queue orderInventoryResultQueue, TopicExchange kekiExchange) {
        return BindingBuilder.bind(orderInventoryResultQueue)
                .to(kekiExchange)
                .with("inventory.result");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
