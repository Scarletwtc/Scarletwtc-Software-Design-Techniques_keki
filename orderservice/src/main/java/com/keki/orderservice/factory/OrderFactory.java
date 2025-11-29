package com.keki.orderservice.factory;

import com.keki.orderservice.builder.CakeBuilder;
import com.keki.orderservice.model.Cake;
import com.keki.orderservice.model.Order;

public class OrderFactory {

    public Order createStandardCakeOrder(String type) {
        CakeBuilder builder = new CakeBuilder()
                .setName(type + " cake");
        Cake cake = builder.build();

        Order order = new Order();
        order.setCake(cake);
        return order;
    }
}


