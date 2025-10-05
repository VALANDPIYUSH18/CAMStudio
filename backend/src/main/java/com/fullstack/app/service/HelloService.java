package com.fullstack.app.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class HelloService {

    public Map<String, Object> getHelloMessage() {
        return getHelloMessage(null);
    }

    public Map<String, Object> getHelloMessage(String name) {
        Map<String, Object> response = new HashMap<>();
        
        if (name != null && !name.trim().isEmpty()) {
            response.put("message", "Hello, " + name + "! Welcome to the Spring Boot API!");
        } else {
            response.put("message", "Hello from Spring Boot! The backend is running successfully.");
        }
        
        response.put("timestamp", LocalDateTime.now());
        response.put("service", "Spring Boot Backend");
        response.put("version", "1.0.0");
        response.put("status", "success");
        
        return response;
    }
}