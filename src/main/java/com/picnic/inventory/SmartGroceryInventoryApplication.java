package com.picnic.inventory;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.Contact;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Smart Grocery Inventory Management System",
        version = "1.0.0",
        description = "A comprehensive inventory management system for grocery warehouses and stores. " +
                     "Features include product management, stock tracking, expiration monitoring, and supplier management.",
        contact = @Contact(
            name = "Development Team",
            email = "dev@example.com"
        )
    )
)
public class SmartGroceryInventoryApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SmartGroceryInventoryApplication.class, args);
        
        System.out.println("==========================================");
        System.out.println("🚀 Smart Grocery Inventory System Started!");
        System.out.println("📊 Application running on: http://localhost:8080");
        System.out.println("📋 API Documentation: http://localhost:8080/swagger-ui.html");
        System.out.println("🔍 Health Check: http://localhost:8080/actuator/health");
        System.out.println("==========================================");
    }
}