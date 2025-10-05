package com.fullstack.app.controller;

import com.fullstack.app.service.HelloService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private HelloService helloService;

    @Test
    void testGetHello() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("message", "Hello from Spring Boot! The backend is running successfully.");
        mockResponse.put("timestamp", LocalDateTime.now());
        mockResponse.put("service", "Spring Boot Backend");
        mockResponse.put("version", "1.0.0");
        mockResponse.put("status", "success");

        when(helloService.getHelloMessage()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/hello"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello from Spring Boot! The backend is running successfully."))
                .andExpect(jsonPath("$.service").value("Spring Boot Backend"))
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testGetHelloWithName() throws Exception {
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("message", "Hello, John! Welcome to the Spring Boot API!");
        mockResponse.put("timestamp", LocalDateTime.now());
        mockResponse.put("service", "Spring Boot Backend");
        mockResponse.put("version", "1.0.0");
        mockResponse.put("status", "success");

        when(helloService.getHelloMessage("John")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/hello/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello, John! Welcome to the Spring Boot API!"));
    }
}