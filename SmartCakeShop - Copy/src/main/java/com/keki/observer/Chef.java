package com.keki.observer;

public class Chef implements Observer {
    private String name;

    public Chef(String name) {
        this.name = name;
    }

    @Override
    public void update(String orderStatus) {
        System.out.println("Chef " + name + " notified: Order is " + orderStatus);
    }
}
