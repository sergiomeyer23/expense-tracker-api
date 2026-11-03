package com.sergiomeyer.expensetracker.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI expenseTrackerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Expense Tracker API")
                .description("REST API for personal expense tracking. Layered Spring Boot 3 service "
                        + "with validated DTOs, RFC 7807 error responses and Flyway-managed PostgreSQL schema.")
                .version("1.0.0")
                .contact(new Contact()
                        .name("Sérgio Meyer")
                        .email("sergio.gabriel.meyer10@gmail.com")
                        .url("https://github.com/sergiomeyer23")));
    }
}
