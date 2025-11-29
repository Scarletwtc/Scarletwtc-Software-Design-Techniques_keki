package com.keki.kitchenservice.client;

import com.keki.kitchenservice.model.KitchenOrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrderClient {

    private static final Logger logger = LoggerFactory.getLogger(OrderClient.class);

    private final RestTemplate restTemplate;
    private final String orderServiceUrl;

    public OrderClient(RestTemplate restTemplate,
                       @Value("${order.service.url}") String orderServiceUrl) {
        this.restTemplate = restTemplate;
        this.orderServiceUrl = orderServiceUrl;
    }

    public void pushStatusToOrderService(Long orderId, KitchenOrderStatus status) {
        String mappedStatus = switch (status) {
            case IN_PROGRESS -> "IN_PROGRESS";
            case READY -> "READY";
            case DELIVERED -> "DELIVERED";
        };

        String url = orderServiceUrl + "/api/orders/" + orderId + "/status?status=" + mappedStatus;
        try {
            restTemplate.postForObject(url, null, Void.class);
            logger.info("Pushed status {} for order {} to order service via POST", mappedStatus, orderId);
        } catch (Exception e) {
            logger.warn("Failed to push status {} for order {} to order service: {}",
                    mappedStatus, orderId, e.getMessage());
        }
    }
}


