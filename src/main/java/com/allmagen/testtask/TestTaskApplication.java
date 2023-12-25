package com.allmagen.testtask;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@OpenAPIDefinition(
        info = @Info(title = "Allmagen", version = "0.0.1", description = "API Documentation.")
)
@SpringBootApplication
@EnableTransactionManagement
public class TestTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestTaskApplication.class, args);
    }
}
