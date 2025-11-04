package com.keki.model;

public abstract class Cake {
    public String name;
    public double price;
    public Flavour flavour;
    public Color color;

    public String getName() { return name; }
    public double getPrice() { return price; }
    public Flavour getFlavour() { return flavour; }
    public Color getColor() { return color; }
}
