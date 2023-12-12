package com.allmagen.testtask;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(title = "Test Task", version = "0.0.1", description = "Test API Documentation.")
)
@SpringBootApplication
public class TestTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestTaskApplication.class, args);
    }
}
