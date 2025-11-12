package com.keki.observer;

public class Staff implements Observer {
    private String name;

    public Staff(String name) {
        this.name = name;
    }

    @Override
    public void update(String orderStatus) {
        System.out.println("Staff " + name + " notified: Order is " + orderStatus);
    }
}
