package com.keki.orderservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class KitchenClient {

    private static final Logger logger = LoggerFactory.getLogger(KitchenClient.class);

    private final RestTemplate restTemplate;
    private final String kitchenServiceUrl;

    public KitchenClient(RestTemplate restTemplate,
                         @Value("${kitchen.service.url}") String kitchenServiceUrl) {
        this.restTemplate = restTemplate;
        this.kitchenServiceUrl = kitchenServiceUrl;
    }

    public Long createKitchenOrder(Long orderId, String cakeName) {
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", orderId);
        request.put("cakeName", cakeName);

        String url = kitchenServiceUrl + "/api/kitchen/orders";

        @SuppressWarnings("unchecked")
        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        if (response == null) {
            logger.warn("Kitchen service returned null response");
            return null;
        }
        Object id = response.get("id");
        if (id instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}


