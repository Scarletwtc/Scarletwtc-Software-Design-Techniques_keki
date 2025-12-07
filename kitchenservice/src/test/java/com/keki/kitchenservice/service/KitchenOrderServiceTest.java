package com.keki.kitchenservice.service;

import com.keki.kitchenservice.config.RabbitConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KitchenOrderServiceTest {

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
