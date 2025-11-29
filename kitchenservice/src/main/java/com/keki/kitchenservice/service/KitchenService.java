package com.keki.kitchenservice.service;

import com.keki.kitchenservice.client.OrderClient;
import com.keki.kitchenservice.dto.CreateKitchenOrderRequest;
import com.keki.kitchenservice.model.KitchenOrder;
import com.keki.kitchenservice.model.KitchenOrderStatus;
import com.keki.kitchenservice.repository.KitchenOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class KitchenService {

    private final KitchenOrderRepository kitchenOrderRepository;
    private final OrderClient orderClient;

    public KitchenService(KitchenOrderRepository kitchenOrderRepository,
                          OrderClient orderClient) {
        this.kitchenOrderRepository = kitchenOrderRepository;
        this.orderClient = orderClient;
    }

    @Transactional
    public KitchenOrder createOrder(CreateKitchenOrderRequest request) {
        KitchenOrder kitchenOrder = new KitchenOrder();
        kitchenOrder.setOrderId(request.getOrderId());
        kitchenOrder.setCakeName(request.getCakeName());
        kitchenOrder.setStatus(KitchenOrderStatus.IN_PROGRESS);

        kitchenOrder = kitchenOrderRepository.save(kitchenOrder);
        orderClient.pushStatusToOrderService(kitchenOrder.getOrderId(), kitchenOrder.getStatus());

        return kitchenOrder;
    }

    public List<KitchenOrder> listAll() {
        return kitchenOrderRepository.findAll();
    }

    @Transactional
    public KitchenOrder markReady(Long id) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kitchen order not found: " + id));
        kitchenOrder.setStatus(KitchenOrderStatus.READY);
        kitchenOrder = kitchenOrderRepository.save(kitchenOrder);
        orderClient.pushStatusToOrderService(kitchenOrder.getOrderId(), kitchenOrder.getStatus());
        return kitchenOrder;
    }

    @Transactional
    public KitchenOrder markDelivered(Long id) {
        KitchenOrder kitchenOrder = kitchenOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Kitchen order not found: " + id));
        kitchenOrder.setStatus(KitchenOrderStatus.DELIVERED);
        kitchenOrder = kitchenOrderRepository.save(kitchenOrder);
        orderClient.pushStatusToOrderService(kitchenOrder.getOrderId(), kitchenOrder.getStatus());
        return kitchenOrder;
    }
}


