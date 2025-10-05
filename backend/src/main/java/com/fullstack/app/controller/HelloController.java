package com.fullstack.app.controller;

import com.fullstack.app.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.cors.origins:http://localhost:3000}")
public class HelloController {

    private final HelloService helloService;

    @Autowired
    public HelloController(HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping("/hello")
    public ResponseEntity<Map<String, Object>> getHello() {
        Map<String, Object> response = helloService.getHelloMessage();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hello/{name}")
    public ResponseEntity<Map<String, Object>> getHelloWithName(@PathVariable String name) {
        Map<String, Object> response = helloService.getHelloMessage(name);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/hello")
    public ResponseEntity<Map<String, Object>> postHello(@RequestBody(required = false) Map<String, String> requestBody) {
        String name = requestBody != null ? requestBody.get("name") : null;
        Map<String, Object> response = helloService.getHelloMessage(name);
        return ResponseEntity.ok(response);
    }
}