package com.keki.kitchenservice.service;

import com.keki.kitchenservice.client.OrderClient;
import com.keki.kitchenservice.dto.OrderConfirmedEvent;
import com.keki.kitchenservice.repository.KitchenOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KitchenOrderEventHandlerTest {

    @Mock
    private KitchenOrderRepository kitchenOrderRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private KitchenOrderEventHandler eventHandler;

    @Test
    void contextLoads() {
        assertNotNull(eventHandler);
    }

    @Test
    void orderConfirmedEventCanBeCreated() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, "CHOCOLATE");
        assertNotNull(event);
        assertEquals(1L, event.getOrderId());
        assertEquals("CHOCOLATE", event.getCakeName());
    }
}
