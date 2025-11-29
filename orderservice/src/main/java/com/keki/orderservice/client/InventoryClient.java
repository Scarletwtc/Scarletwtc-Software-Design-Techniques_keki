package com.keki.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class InventoryClient {

    private static final Logger logger = LoggerFactory.getLogger(InventoryClient.class);

    private final RestTemplate restTemplate;
    private final String inventoryServiceUrl;

    public InventoryClient(RestTemplate restTemplate,
                           @Value("${inventory.service.url}") String inventoryServiceUrl) {
        this.restTemplate = restTemplate;
        this.inventoryServiceUrl = inventoryServiceUrl;
    }

    public boolean checkAndReserveStandardCakeIngredients(int quantity) {
        Map<String, Integer> items = new HashMap<>();
        items.put("Flour", 2 * quantity);
        items.put("Sugar", 1 * quantity);
        items.put("Egg", 3 * quantity);

        Map<String, Object> request = new HashMap<>();
        request.put("items", items);

        String url = inventoryServiceUrl + "/api/inventory/check-and-reserve";
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            if (response == null) {
                return false;
            }
            Object success = response.get("success");
            return success instanceof Boolean && (Boolean) success;
        } catch (HttpClientErrorException e) {
            logger.warn("Inventory check failed: {}", e.getStatusCode());
            return false;
        }
    }
}


