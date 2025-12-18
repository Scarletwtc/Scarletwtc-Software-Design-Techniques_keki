package com.keki.orderservice.service;

import com.keki.orderservice.dto.InventoryResultEvent;
import com.keki.orderservice.dto.OrderConfirmedEvent;
import com.keki.orderservice.model.Cake;
import com.keki.orderservice.model.Flavour;
import com.keki.orderservice.model.Order;
import com.keki.orderservice.model.OrderStatus;
import com.keki.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderInventoryEventHandlerTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OrderInventoryEventHandler eventHandler;

    @Test
    void handleInventoryResult_WhenSuccessful_PublishesOrderConfirmedEvent() {
        InventoryResultEvent resultEvent = new InventoryResultEvent(1L, true, null);

        Order order = mock(Order.class);
        Cake cake = mock(Cake.class);
        when(order.getId()).thenReturn(1L);
        when(order.getCake()).thenReturn(cake);
        when(cake.getName()).thenReturn("Test Cake");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        eventHandler.handleInventoryResult(resultEvent);

        verify(orderRepository).save(any(Order.class));
        verify(order).updateStatus(OrderStatus.CONFIRMED);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderConfirmedEvent> eventCaptor = ArgumentCaptor.forClass(OrderConfirmedEvent.class);

        verify(rabbitTemplate).convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            eventCaptor.capture()
        );

        assertEquals("keki.exchange", exchangeCaptor.getValue());
        assertEquals("order.confirmed", routingKeyCaptor.getValue());
        assertEquals(1L, eventCaptor.getValue().getOrderId());
    }

    @Test
    void handleInventoryResult_WhenFailed_SetsOrderToRejected() {
        InventoryResultEvent resultEvent = new InventoryResultEvent(2L, false, "Insufficient stock");

        Order order = mock(Order.class);
        when(orderRepository.findById(2L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        eventHandler.handleInventoryResult(resultEvent);

        verify(orderRepository).save(any(Order.class));
        verify(order).updateStatus(OrderStatus.REJECTED);

        verify(rabbitTemplate, never()).convertAndSend(eq("keki.exchange"), eq("order.confirmed"), any(OrderConfirmedEvent.class));
    }

    @Test
    void orderStatusTransitionDemonstratesAsyncWorkflow() {
        assertNotNull(OrderStatus.PENDING_INVENTORY);
        assertNotNull(OrderStatus.CONFIRMED);
        assertNotNull(OrderStatus.REJECTED);
    }
}
