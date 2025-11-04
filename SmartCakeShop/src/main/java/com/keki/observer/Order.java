package com.keki.observer;

import com.keki.model.Cake;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private List<Observer> observers = new ArrayList<>();
    private String status;
    private Cake cake;

    public Order(Cake cake) {
        this.cake = cake;
    }

    public void attach(Observer obs) {
        observers.add(obs);
    }

    public void detach(Observer obs) {
        observers.remove(obs);
    }

    public void notifyObservers() {
        for (Observer obs : observers) {
            obs.update(status);
        }
    }

    public void setStatus(String status) {
        this.status = status;
        notifyObservers();
    }

    public String getStatus() {
        return status;
    }

    public Cake getCake() {
        return cake;
    }
}
