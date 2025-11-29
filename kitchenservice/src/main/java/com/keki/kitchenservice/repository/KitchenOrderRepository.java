package com.keki.kitchenservice.repository;

import com.keki.kitchenservice.model.KitchenOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KitchenOrderRepository extends JpaRepository<KitchenOrder, Long> {
}


