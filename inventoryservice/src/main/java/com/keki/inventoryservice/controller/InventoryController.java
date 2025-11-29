package com.keki.inventoryservice.controller;

import com.keki.inventoryservice.dto.CheckAndReserveRequest;
import com.keki.inventoryservice.dto.CheckAndReserveResponse;
import com.keki.inventoryservice.model.Ingredient;
import com.keki.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public List<Ingredient> listAll() {
        return inventoryService.listAll();
    }

    @PostMapping
    public Ingredient create(@RequestBody Ingredient ingredient) {
        return inventoryService.save(ingredient);
    }

    @PostMapping("/check-and-reserve")
    public ResponseEntity<CheckAndReserveResponse> checkAndReserve(
            @Valid @RequestBody CheckAndReserveRequest request) {
        CheckAndReserveResponse response = inventoryService.checkAndReserve(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}


