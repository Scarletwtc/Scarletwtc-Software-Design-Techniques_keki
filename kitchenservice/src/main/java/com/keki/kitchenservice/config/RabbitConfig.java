package com.keki.kitchenservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE_NAME = "keki.exchange";
    public static final String QUEUE_ORDER_CONFIRMED = "kitchen.order-confirmed";

    @Bean
    public TopicExchange kekiExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue kitchenOrderConfirmedQueue() {
        return new Queue(QUEUE_ORDER_CONFIRMED, true);
    }

    @Bean
    public Binding bindOrderConfirmed(Queue kitchenOrderConfirmedQueue, TopicExchange kekiExchange) {
        return BindingBuilder.bind(kitchenOrderConfirmedQueue)
                .to(kekiExchange)
                .with("order.confirmed");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
