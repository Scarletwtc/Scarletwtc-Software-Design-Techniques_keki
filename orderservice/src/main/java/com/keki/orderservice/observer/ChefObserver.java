package com.keki.orderservice.observer;

import com.keki.orderservice.model.Order;
import com.keki.orderservice.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChefObserver implements OrderObserver {

    private static final Logger logger = LoggerFactory.getLogger(ChefObserver.class);

    @Override
    public void onStatusChanged(Order order) {
        OrderStatus status = order.getStatus();
        logger.info("Chef notified: order {} is now {}", order.getId(), status);
    }
}


