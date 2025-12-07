package com.keki.inventoryservice.service;

import com.keki.inventoryservice.config.RabbitConfig;
import com.keki.inventoryservice.dto.InventoryResultEvent;
import com.keki.inventoryservice.dto.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InventoryEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(InventoryEventHandler.class);

    private final InventoryService inventoryService;
    private final RabbitTemplate rabbitTemplate;

    public InventoryEventHandler(InventoryService inventoryService, RabbitTemplate rabbitTemplate) {
        this.inventoryService = inventoryService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_ORDER_PLACED)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        logger.info("Received order.placed event for order {}", event.getOrderId());

        Map<String, Integer> items = new HashMap<>();
        items.put("Flour", 2 * event.getQuantity());
        items.put("Sugar", 1 * event.getQuantity());
        items.put("Egg", 3 * event.getQuantity());

        boolean success = inventoryService.checkAndReserve(items);

        InventoryResultEvent result = new InventoryResultEvent(
                event.getOrderId(),
                success,
                success ? null : "Insufficient ingredients in stock"
        );

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE_NAME,
                "inventory.result",
                result
        );

        logger.info("Sent inventory.result for order {}: success={}", event.getOrderId(), success);
    }
}
