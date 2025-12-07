package com.keki.orderservice.service;

import com.keki.orderservice.client.KitchenClient;
import com.keki.orderservice.config.RabbitConfig;
import com.keki.orderservice.dto.InventoryResultEvent;
import com.keki.orderservice.model.Order;
import com.keki.orderservice.model.OrderStatus;
import com.keki.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderInventoryEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(OrderInventoryEventHandler.class);

    private final OrderRepository orderRepository;
    private final KitchenClient kitchenClient;

    public OrderInventoryEventHandler(OrderRepository orderRepository, KitchenClient kitchenClient) {
        this.orderRepository = orderRepository;
        this.kitchenClient = kitchenClient;
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
            
            // Now send to kitchen (HTTP call as before)
            Long kitchenOrderId = kitchenClient.createKitchenOrder(order.getId(), order.getCake().getName());
            order.setKitchenOrderId(kitchenOrderId);
            order.updateStatus(OrderStatus.IN_PROGRESS);
            orderRepository.save(order);
            
            logger.info("Order {} confirmed and sent to kitchen", order.getId());
        } else {
            order.updateStatus(OrderStatus.REJECTED);
            orderRepository.save(order);
            
            logger.warn("Order {} rejected: {}", order.getId(), event.getReason());
        }
    }
}
