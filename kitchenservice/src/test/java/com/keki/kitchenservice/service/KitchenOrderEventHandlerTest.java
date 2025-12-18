package com.keki.kitchenservice.service;

import com.keki.kitchenservice.client.OrderClient;
import com.keki.kitchenservice.dto.OrderConfirmedEvent;
import com.keki.kitchenservice.model.KitchenOrder;
import com.keki.kitchenservice.repository.KitchenOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KitchenOrderEventHandlerTest {

    @Mock
    private KitchenOrderRepository kitchenOrderRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private KitchenOrderEventHandler eventHandler;

    @Test
    void handleOrderConfirmed_CreatesKitchenOrderAndNotifiesOrderService() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(1L, "CHOCOLATE");

        when(kitchenOrderRepository.save(any(KitchenOrder.class))).thenAnswer(invocation -> {
            KitchenOrder order = invocation.getArgument(0);
            // Simulate database generating the ID using reflection
            java.lang.reflect.Field idField = KitchenOrder.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(order, 100L);
            return order;
        });

        eventHandler.handleOrderConfirmed(event);

        ArgumentCaptor<KitchenOrder> orderCaptor = ArgumentCaptor.forClass(KitchenOrder.class);
        verify(kitchenOrderRepository).save(orderCaptor.capture());

        KitchenOrder capturedOrder = orderCaptor.getValue();
        assertEquals(1L, capturedOrder.getOrderId());
        assertEquals("CHOCOLATE", capturedOrder.getCakeName());

        verify(orderClient).updateKitchenOrderId(anyLong(), anyLong());
        verify(orderClient).pushStatusToOrderService(anyLong(), any());
    }

    @Test
    void handleOrderConfirmed_DemonstratesDecouplingViaMessageQueue() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(2L, "VANILLA");

        KitchenOrder savedOrder = new KitchenOrder();
        when(kitchenOrderRepository.save(any(KitchenOrder.class))).thenReturn(savedOrder);

        eventHandler.handleOrderConfirmed(event);

        verify(kitchenOrderRepository).save(any(KitchenOrder.class));
    }

    @Test
    void orderConfirmedEventRepresentsAsyncCommunication() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(3L, "STRAWBERRY");
        assertNotNull(event);
        assertEquals(3L, event.getOrderId());
        assertEquals("STRAWBERRY", event.getCakeName());
    }
}
