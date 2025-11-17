package com.example.vagas.controller;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(originPatterns = "*")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5);
                
                response.put("status", "OK");
                response.put("message", "API Spring Boot funcionando corretamente");
                response.put("database", isValid ? "CONNECTED" : "DISCONNECTED");
                response.put("timestamp", System.currentTimeMillis());
                response.put("port", 8080);
                
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", "Erro na conexão com banco");
            response.put("error", e.getMessage());
            response.put("database", "DISCONNECTED");
            
            return ResponseEntity.status(503).body(response);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("application", "vagas-api");
        info.put("version", "1.0.0");
        info.put("description", "API para plataforma de vagas e cursos");
        info.put("endpoints", Map.of(
            "cursos", "/api/cursos",
            "vagas", "/api/vagas", 
            "candidaturas", "/api/candidaturas",
            "health", "/api/health"
        ));
        
        return ResponseEntity.ok(info);
    }
}