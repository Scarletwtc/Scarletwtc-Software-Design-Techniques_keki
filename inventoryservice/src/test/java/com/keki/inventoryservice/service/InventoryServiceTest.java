package com.keki.inventoryservice.service;

import com.keki.inventoryservice.config.RabbitConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InventoryServiceTest {

    @Test
    void rabbitConfigCanBeInstantiated() {
        RabbitConfig config = new RabbitConfig();
        assertNotNull(config);
    }

    @Test
    void simpleTest() {
        assertTrue(true);
    }
}
