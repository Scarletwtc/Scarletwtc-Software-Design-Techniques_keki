package com.keki.orderservice.dto;

public class OrderConfirmedEvent {
    private Long orderId;
    private String cakeName;

    public OrderConfirmedEvent() {
    }

    public OrderConfirmedEvent(Long orderId, String cakeName) {
        this.orderId = orderId;
        this.cakeName = cakeName;
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
}
