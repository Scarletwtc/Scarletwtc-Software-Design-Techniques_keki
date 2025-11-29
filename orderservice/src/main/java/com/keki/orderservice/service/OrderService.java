package com.keki.orderservice.service;

import com.keki.orderservice.client.InventoryClient;
import com.keki.orderservice.client.KitchenClient;
import com.keki.orderservice.dto.CreateOrderRequest;
import com.keki.orderservice.dto.OrderResponse;
import com.keki.orderservice.factory.OrderFactory;
import com.keki.orderservice.model.Cake;
import com.keki.orderservice.model.Order;
import com.keki.orderservice.model.OrderStatus;
import com.keki.orderservice.observer.ChefObserver;
import com.keki.orderservice.observer.StaffObserver;
import com.keki.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final KitchenClient kitchenClient;
    private final OrderFactory orderFactory = new OrderFactory();

    public OrderService(OrderRepository orderRepository,
                        InventoryClient inventoryClient,
                        KitchenClient kitchenClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.kitchenClient = kitchenClient;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        boolean reserved = inventoryClient.checkAndReserveStandardCakeIngredients(request.getQuantity());
        if (!reserved) {
            throw new IllegalStateException("Insufficient inventory for standard cake");
        }

        Order order = orderFactory.createStandardCakeOrder("Standard");
        order.setCustomerName(request.getCustomerName());

        Cake cake = order.getCake();
        cake.setFlavour(request.getFlavour());
        cake.setColor(request.getColor());

        order.attach(new ChefObserver());
        order.attach(new StaffObserver());

        order.updateStatus(OrderStatus.IN_PROGRESS);

        order = orderRepository.save(order);

        Long kitchenOrderId = kitchenClient.createKitchenOrder(order.getId(), cake.getName());
        order.setKitchenOrderId(kitchenOrderId);
        order = orderRepository.save(order);

        return toResponse(order);
    }

    public List<OrderResponse> listOrders() {
        return orderRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        return toResponse(order);
    }

    @Transactional
    public void updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
        order.updateStatus(status);
        orderRepository.save(order);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        if (order.getCake() != null) {
            response.setCakeName(order.getCake().getName());
            response.setFlavour(order.getCake().getFlavour());
            response.setColor(order.getCake().getColor());
            response.setPrice(order.getCake().getPrice());
        }
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}


