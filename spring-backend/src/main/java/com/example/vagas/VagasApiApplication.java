package com.example.vagas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VagasApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(VagasApiApplication.class, args);
        System.out.println("=== Spring Boot 3.2.0 Application Started Successfully ===");
        System.out.println("Server running on: http://localhost:8081");
        System.out.println("API endpoints available at: http://localhost:8081/api/");
        System.out.println("Health check: http://localhost:8081/api/health");
    }
}