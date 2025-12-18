package com.keki.orderservice.service;

import com.keki.orderservice.client.KitchenClient;
import com.keki.orderservice.config.RabbitConfig;
import com.keki.orderservice.dto.CreateOrderRequest;
import com.keki.orderservice.dto.OrderPlacedEvent;
import com.keki.orderservice.model.Cake;
import com.keki.orderservice.model.Color;
import com.keki.orderservice.model.Flavour;
import com.keki.orderservice.model.Order;
import com.keki.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KitchenClient kitchenClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_PublishesOrderPlacedEventToRabbitMQ() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setCustomerName("Test Customer");
        request.setFlavour(Flavour.CHOCOLATE);
        request.setColor(Color.BROWN);
        request.setQuantity(2);

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            return order;
        });

        orderService.createOrder(request);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);

        verify(rabbitTemplate).convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            eventCaptor.capture()
        );

        assertEquals("keki.exchange", exchangeCaptor.getValue());
        assertEquals("order.placed", routingKeyCaptor.getValue());
        OrderPlacedEvent event = eventCaptor.getValue();
        assertEquals("Standard cake", event.getCakeName());
        assertEquals(2, event.getQuantity());
    }

    @Test
    void rabbitConfigDefinesCorrectExchangeAndQueues() {
        RabbitConfig config = new RabbitConfig();
        assertNotNull(config);
        assertEquals("keki.exchange", RabbitConfig.EXCHANGE_NAME);
        assertEquals("inventory.order-placed", RabbitConfig.QUEUE_ORDER_PLACED);
        assertEquals("order.inventory-result", RabbitConfig.QUEUE_INVENTORY_RESULT);
    }
}
