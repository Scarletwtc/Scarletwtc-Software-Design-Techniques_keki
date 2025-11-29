package com.keki.inventoryservice.dto;

import java.util.Map;

public class CheckAndReserveResponse {

    private boolean success;
    private Map<String, Integer> missingItems;

    public CheckAndReserveResponse() {
    }

    public CheckAndReserveResponse(boolean success, Map<String, Integer> missingItems) {
        this.success = success;
        this.missingItems = missingItems;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, Integer> getMissingItems() {
        return missingItems;
    }

    public void setMissingItems(Map<String, Integer> missingItems) {
        this.missingItems = missingItems;
    }
}


