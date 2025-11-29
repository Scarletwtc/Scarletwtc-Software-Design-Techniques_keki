package com.keki.orderservice.observer;

import com.keki.orderservice.model.Order;

public interface OrderObserver {

    void onStatusChanged(Order order);
}


