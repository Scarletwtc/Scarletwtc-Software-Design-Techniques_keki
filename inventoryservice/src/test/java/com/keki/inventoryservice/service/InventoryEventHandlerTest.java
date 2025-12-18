package com.keki.inventoryservice.service;

import com.keki.inventoryservice.dto.CheckAndReserveRequest;
import com.keki.inventoryservice.dto.CheckAndReserveResponse;
import com.keki.inventoryservice.dto.InventoryResultEvent;
import com.keki.inventoryservice.dto.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryEventHandlerTest {

    @Mock
    private InventoryService inventoryService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private InventoryEventHandler eventHandler;

    @Test
    void handleOrderPlaced_WhenStockAvailable_PublishesSuccessResult() {
        OrderPlacedEvent orderEvent = new OrderPlacedEvent(1L, "CHOCOLATE", 2);

        when(inventoryService.checkAndReserve(anyMap())).thenReturn(true);

        eventHandler.handleOrderPlaced(orderEvent);

        ArgumentCaptor<String> exchangeCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> routingKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<InventoryResultEvent> eventCaptor = ArgumentCaptor.forClass(InventoryResultEvent.class);

        verify(rabbitTemplate).convertAndSend(
            exchangeCaptor.capture(),
            routingKeyCaptor.capture(),
            eventCaptor.capture()
        );

        assertEquals("keki.exchange", exchangeCaptor.getValue());
        assertEquals("inventory.result", routingKeyCaptor.getValue());
        InventoryResultEvent result = eventCaptor.getValue();
        assertEquals(1L, result.getOrderId());
        assertTrue(result.isSuccess());
        assertNull(result.getReason());
    }

    @Test
    void handleOrderPlaced_WhenStockInsufficient_PublishesFailureResult() {
        OrderPlacedEvent orderEvent = new OrderPlacedEvent(2L, "VANILLA", 10);

        when(inventoryService.checkAndReserve(anyMap())).thenReturn(false);

        eventHandler.handleOrderPlaced(orderEvent);

        ArgumentCaptor<InventoryResultEvent> eventCaptor = ArgumentCaptor.forClass(InventoryResultEvent.class);
        verify(rabbitTemplate).convertAndSend(
            eq("keki.exchange"),
            eq("inventory.result"),
            eventCaptor.capture()
        );

        InventoryResultEvent result = eventCaptor.getValue();
        assertEquals(2L, result.getOrderId());
        assertFalse(result.isSuccess());
        assertEquals("Insufficient ingredients in stock", result.getReason());
    }

    @Test
    void orderPlacedEventCalculatesIngredientRequirements() {
        OrderPlacedEvent event = new OrderPlacedEvent(3L, "STRAWBERRY", 3);
        assertEquals(3L, event.getOrderId());
        assertEquals("STRAWBERRY", event.getCakeName());
        assertEquals(3, event.getQuantity());
    }
}
