package com.keki.inventoryservice.dto;

public class OrderPlacedEvent {
    private Long orderId;
    private String cakeName;
    private int quantity;

    public OrderPlacedEvent() {
    }

    public OrderPlacedEvent(Long orderId, String cakeName, int quantity) {
        this.orderId = orderId;
        this.cakeName = cakeName;
        this.quantity = quantity;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getCakeName() {
        return cakeName;
    }

    public void setCakeName(String cakeName) {
        this.cakeName = cakeName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
