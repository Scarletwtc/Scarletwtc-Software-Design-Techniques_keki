package com.keki.orderservice.controller;

import com.keki.orderservice.dto.CreateOrderRequest;
import com.keki.orderservice.dto.OrderResponse;
import com.keki.orderservice.model.OrderStatus;
import com.keki.orderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        try {
            OrderResponse response = orderService.createOrder(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public List<OrderResponse> listOrders() {
        return orderService.listOrders();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id,
                                             @RequestParam("status") OrderStatus status) {
        orderService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    // Extra entry point so other services can safely call POST instead of PATCH
    @PostMapping("/{id}/status")
    public ResponseEntity<Void> updateStatusPost(@PathVariable Long id,
                                                 @RequestParam("status") OrderStatus status) {
        orderService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}


