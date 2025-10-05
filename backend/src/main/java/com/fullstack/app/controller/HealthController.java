package com.fullstack.app.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.cors.origins:http://localhost:3000}")
public class HealthController {

    @Value("${spring.application.name:fullstack-app-backend}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String serverPort;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getHealth() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("application", applicationName);
        response.put("port", serverPort);
        response.put("timestamp", LocalDateTime.now());
        response.put("uptime", System.currentTimeMillis() - getStartTime());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("application", applicationName);
        response.put("version", "1.0.0");
        response.put("description", "Backend API server built with Spring Boot");
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("springBootVersion", org.springframework.boot.SpringBootVersion.getVersion());
        response.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(response);
    }

    private long getStartTime() {
        return System.currentTimeMillis() - (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
    }
}