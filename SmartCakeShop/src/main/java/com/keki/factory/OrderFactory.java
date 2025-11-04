package com.keki.factory;

import com.keki.builder.CakeBuilder;
import com.keki.model.Cake;
import com.keki.observer.Order;

public class OrderFactory {

    public Order createOrder(String type) {
        CakeBuilder builder = new CakeBuilder();
        builder.setName(type + " Cake").setPrice(50);
        Cake cake = builder.build();
        return new Order(cake);
    }
}
