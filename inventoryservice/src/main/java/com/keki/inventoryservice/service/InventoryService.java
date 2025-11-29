package com.keki.inventoryservice.service;

import com.keki.inventoryservice.dto.CheckAndReserveRequest;
import com.keki.inventoryservice.dto.CheckAndReserveResponse;
import com.keki.inventoryservice.model.Ingredient;
import com.keki.inventoryservice.repository.IngredientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InventoryService {

    private final IngredientRepository ingredientRepository;

    public InventoryService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> listAll() {
        return ingredientRepository.findAll();
    }

    public Ingredient save(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    @Transactional
    public CheckAndReserveResponse checkAndReserve(CheckAndReserveRequest request) {
        Map<String, Integer> missing = new HashMap<>();

        for (Map.Entry<String, Integer> entry : request.getItems().entrySet()) {
            String name = entry.getKey();
            int requestedQty = entry.getValue();

            Ingredient ingredient = ingredientRepository.findByName(name)
                    .orElseGet(() -> new Ingredient(name, 0));

            if (ingredient.getId() == null) {
                missing.put(name, requestedQty);
            } else if (ingredient.getQuantity() < requestedQty) {
                missing.put(name, requestedQty - ingredient.getQuantity());
            }
        }

        if (!missing.isEmpty()) {
            return new CheckAndReserveResponse(false, missing);
        }

        for (Map.Entry<String, Integer> entry : request.getItems().entrySet()) {
            String name = entry.getKey();
            int requestedQty = entry.getValue();
            Ingredient ingredient = ingredientRepository.findByName(name)
                    .orElseThrow();
            ingredient.setQuantity(ingredient.getQuantity() - requestedQty);
            ingredientRepository.save(ingredient);
        }

        return new CheckAndReserveResponse(true, Map.of());
    }
}


