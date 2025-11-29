package com.keki.kitchenservice.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "kitchen_orders")
public class KitchenOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    private String cakeName;

    @Enumerated(EnumType.STRING)
    private KitchenOrderStatus status;

    private Instant createdAt;

    private Instant updatedAt;

    public KitchenOrder() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.status = KitchenOrderStatus.IN_PROGRESS;
    }

    public Long getId() {
        return id;
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

    public KitchenOrderStatus getStatus() {
        return status;
    }

    public void setStatus(KitchenOrderStatus status) {
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}


