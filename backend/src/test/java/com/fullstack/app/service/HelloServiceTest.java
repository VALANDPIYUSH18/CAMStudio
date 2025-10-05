package com.fullstack.app.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HelloServiceTest {

    private final HelloService helloService = new HelloService();

    @Test
    void testGetHelloMessage() {
        Map<String, Object> response = helloService.getHelloMessage();
        
        assertNotNull(response);
        assertTrue(response.containsKey("message"));
        assertTrue(response.containsKey("timestamp"));
        assertTrue(response.containsKey("service"));
        assertTrue(response.containsKey("version"));
        assertTrue(response.containsKey("status"));
        
        assertEquals("Hello from Spring Boot! The backend is running successfully.", response.get("message"));
        assertEquals("Spring Boot Backend", response.get("service"));
        assertEquals("1.0.0", response.get("version"));
        assertEquals("success", response.get("status"));
    }

    @Test
    void testGetHelloMessageWithName() {
        Map<String, Object> response = helloService.getHelloMessage("Alice");
        
        assertNotNull(response);
        assertTrue(response.containsKey("message"));
        assertEquals("Hello, Alice! Welcome to the Spring Boot API!", response.get("message"));
        assertEquals("Spring Boot Backend", response.get("service"));
        assertEquals("1.0.0", response.get("version"));
        assertEquals("success", response.get("status"));
    }

    @Test
    void testGetHelloMessageWithEmptyName() {
        Map<String, Object> response = helloService.getHelloMessage("");
        
        assertNotNull(response);
        assertEquals("Hello from Spring Boot! The backend is running successfully.", response.get("message"));
    }

    @Test
    void testGetHelloMessageWithNullName() {
        Map<String, Object> response = helloService.getHelloMessage(null);
        
        assertNotNull(response);
        assertEquals("Hello from Spring Boot! The backend is running successfully.", response.get("message"));
    }
}