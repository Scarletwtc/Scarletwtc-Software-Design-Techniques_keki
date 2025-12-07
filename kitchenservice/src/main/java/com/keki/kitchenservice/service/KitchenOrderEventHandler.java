package com.keki.kitchenservice.service;

import com.keki.kitchenservice.client.OrderClient;
import com.keki.kitchenservice.config.RabbitConfig;
import com.keki.kitchenservice.dto.OrderConfirmedEvent;
import com.keki.kitchenservice.model.KitchenOrder;
import com.keki.kitchenservice.model.KitchenOrderStatus;
import com.keki.kitchenservice.repository.KitchenOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KitchenOrderEventHandler {

    private static final Logger logger = LoggerFactory.getLogger(KitchenOrderEventHandler.class);

    private final KitchenOrderRepository kitchenOrderRepository;
    private final OrderClient orderClient;

    public KitchenOrderEventHandler(KitchenOrderRepository kitchenOrderRepository, OrderClient orderClient) {
        this.kitchenOrderRepository = kitchenOrderRepository;
        this.orderClient = orderClient;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_ORDER_CONFIRMED)
    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        logger.info("Received order.confirmed event for order {}", event.getOrderId());

        KitchenOrder kitchenOrder = new KitchenOrder();
        kitchenOrder.setOrderId(event.getOrderId());
        kitchenOrder.setCakeName(event.getCakeName());
        kitchenOrder.setStatus(KitchenOrderStatus.IN_PROGRESS);
        
        kitchenOrder = kitchenOrderRepository.save(kitchenOrder);
        
        orderClient.updateKitchenOrderId(event.getOrderId(), kitchenOrder.getId());
        orderClient.pushStatusToOrderService(event.getOrderId(), KitchenOrderStatus.IN_PROGRESS);
        
        logger.info("Kitchen order {} created for order {} via RabbitMQ", kitchenOrder.getId(), event.getOrderId());
    }
}
