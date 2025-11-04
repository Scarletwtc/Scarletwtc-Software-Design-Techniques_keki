package com.keki;

import com.keki.builder.CakeBuilder;
import com.keki.factory.OrderFactory;
import com.keki.model.Cake;
import com.keki.model.Color;
import com.keki.model.Flavour;
import com.keki.observer.*;
import com.keki.singleton.InventoryManager;

public class Main {
    public static void main(String[] args) {

        // 1. Create Chef and Staff
        Chef chef = new Chef("Andra");
        Staff staff = new Staff("Abd");

        // 2. Manager creates an Order using Factory + Builder
        OrderFactory factory = new OrderFactory();
        Order order = factory.createOrder("Birthday");

        // Optional: customize Cake via Builder
        CakeBuilder builder = new CakeBuilder();
        builder.setName("Birthday Cake")
                .setPrice(70)
                .setFlavour(Flavour.VANILLA)
                .setColor(Color.PINK);
        Cake customCake = builder.build();
        order = new Order(customCake);

        // 3. Attach observers
        order.attach(chef);
        order.attach(staff);

        // 4. Check inventory using Singleton
        InventoryManager inventory = InventoryManager.getInstance();
        System.out.println("Flour stock before: " + inventory.checkStock("Flour"));

        // 5. Update order status -> observers get notified
        order.setStatus("IN_PROGRESS");

        // 6. Chef updates inventory after preparing cake
        inventory.updateStock("Flour", -2);
        inventory.updateStock("Sugar", -1);
        inventory.updateStock("Egg", -3);
        inventory.updateStock("Coffee", -4);
        inventory.updateStock("BakingSoda", -5);
        inventory.updateStock("Milk", -6);

        // 7. Order completed
        order.setStatus("READY");

        System.out.println("Flour stock after: " + inventory.checkStock("Flour"));
        System.out.println("Sugar stock after: " + inventory.checkStock("Sugar"));
        System.out.println("Egg stock after: " + inventory.checkStock("Egg"));
        System.out.println("Coffee stock after: " + inventory.checkStock("Coffee"));
        System.out.println("Milk stock after: " + inventory.checkStock("Milk    "));




        // 8. Staff delivers cake
        System.out.println("Cake delivered: " + order.getCake().getName() +
                " (" + order.getCake().getFlavour() +
                ", " + order.getCake().getColor() + ")");
    }
}
