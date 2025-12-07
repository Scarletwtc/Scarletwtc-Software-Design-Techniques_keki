package com.keki.orderservice.service;

import com.keki.orderservice.config.RabbitConfig;
import com.keki.orderservice.dto.InventoryResultEvent;
import com.keki.orderservice.dto.OrderConfirmedEvent;
import com.keki.orderservice.model.Order;
import com.keki.orderservice.model.OrderStatus;
import com.keki.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderInventoryEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderInventoryEventHandler.class);

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;

    public OrderInventoryEventHandler(OrderRepository orderRepository, RabbitTemplate rabbitTemplate) {
        this.orderRepository = orderRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_INVENTORY_RESULT)
    @Transactional
    public void handleInventoryResult(InventoryResultEvent event) {
        logger.info("Received inventory result for order {}: success={}", event.getOrderId(), event.isSuccess());

        Order order = orderRepository.findById(event.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + event.getOrderId()));

        if (event.isSuccess()) {
            // Inventory reserved successfully
            order.updateStatus(OrderStatus.CONFIRMED);
            order = orderRepository.save(order);
            
            // Publish order.confirmed event to RabbitMQ for KitchenService
            OrderConfirmedEvent confirmedEvent = new OrderConfirmedEvent(
                    order.getId(),
                    order.getCake().getName()
            );
            
            rabbitTemplate.convertAndSend(
                    RabbitConfig.EXCHANGE_NAME,
                    "order.confirmed",
                    confirmedEvent
            );
            
            logger.info("Order {} confirmed and published to kitchen queue", order.getId());
        } else {
            order.updateStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
            
            logger.warn("Order {} rejected: {}", order.getId(), event.getReason());
        }
    }
}
