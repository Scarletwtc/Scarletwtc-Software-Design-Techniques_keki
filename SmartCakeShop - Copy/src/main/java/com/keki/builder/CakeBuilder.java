package com.keki.builder;

import com.keki.model.Cake;
import com.keki.model.Color;
import com.keki.model.Flavour;

public class CakeBuilder {
    private Cake cake;

    public CakeBuilder() {
        this.cake = new Cake() {};

        //  default values
        this.cake.name = "Chocolate Cake";
        this.cake.price = 50;
        this.cake.flavour = Flavour.CHOCOLATE;
        this.cake.color = Color.BROWN;
    }

    public CakeBuilder setName(String name) {
        cake.name = name;
        return this;
    }

    public CakeBuilder setPrice(double price) {
        cake.price = price;
        return this;
    }

    public CakeBuilder setFlavour(Flavour flavour) {
        cake.flavour = flavour;
        return this;
    }

    public CakeBuilder setColor(Color color) {
        cake.color = color;
        return this;
    }

    public Cake build() {
        return cake;
    }
}
