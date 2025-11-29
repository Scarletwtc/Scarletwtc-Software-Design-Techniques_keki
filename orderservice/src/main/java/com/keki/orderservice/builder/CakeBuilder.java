package com.keki.orderservice.builder;

import com.keki.orderservice.model.Cake;
import com.keki.orderservice.model.Color;
import com.keki.orderservice.model.Flavour;

public class CakeBuilder {

    private final Cake cake;

    public CakeBuilder() {
        this.cake = new Cake();
        this.cake.setName("Chocolate Cake");
        this.cake.setPrice(50.0);
        this.cake.setFlavour(Flavour.CHOCOLATE);
        this.cake.setColor(Color.BROWN);
    }

    public CakeBuilder setName(String name) {
        cake.setName(name);
        return this;
    }

    public CakeBuilder setPrice(double price) {
        cake.setPrice(price);
        return this;
    }

    public CakeBuilder setFlavour(Flavour flavour) {
        cake.setFlavour(flavour);
        return this;
    }

    public CakeBuilder setColor(Color color) {
        cake.setColor(color);
        return this;
    }

    public Cake build() {
        return cake;
    }
}


