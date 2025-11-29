package com.keki.orderservice.observer;

import com.keki.orderservice.model.Order;
import com.keki.orderservice.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StaffObserver implements OrderObserver {

    private static final Logger logger = LoggerFactory.getLogger(StaffObserver.class);

    @Override
    public void onStatusChanged(Order order) {
        OrderStatus status = order.getStatus();
        logger.info("Staff notified: order {} is now {}", order.getId(), status);
    }
}


