package com.keki.kitchenservice.controller;

import com.keki.kitchenservice.dto.CreateKitchenOrderRequest;
import com.keki.kitchenservice.model.KitchenOrder;
import com.keki.kitchenservice.service.KitchenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kitchen/orders")
public class KitchenController {

    private final KitchenService kitchenService;

    public KitchenController(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @PostMapping
    public ResponseEntity<KitchenOrder> create(@Valid @RequestBody CreateKitchenOrderRequest request) {
        KitchenOrder created = kitchenService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<KitchenOrder> listAll() {
        return kitchenService.listAll();
    }

    @PatchMapping("/{id}/ready")
    public KitchenOrder markReady(@PathVariable Long id) {
        return kitchenService.markReady(id);
    }

    @PatchMapping("/{id}/delivered")
    public KitchenOrder markDelivered(@PathVariable Long id) {
        return kitchenService.markDelivered(id);
    }
}


