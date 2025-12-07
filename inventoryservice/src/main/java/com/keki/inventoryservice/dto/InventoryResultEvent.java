package com.keki.inventoryservice.dto;

public class InventoryResultEvent {
    private Long orderId;
    private boolean success;
    private String reason;

    public InventoryResultEvent() {
    }

    public InventoryResultEvent(Long orderId, boolean success, String reason) {
        this.orderId = orderId;
        this.success = success;
        this.reason = reason;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
