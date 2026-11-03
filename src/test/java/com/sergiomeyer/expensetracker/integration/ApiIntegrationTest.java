package com.sergiomeyer.expensetracker.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full-stack tests: real controllers, service, JPA and the Flyway-migrated schema on H2.
 * Hibernate runs with ddl-auto=validate, so these also prove the migration matches the entity.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void createsAndReadsBackAnExpense() throws Exception {
        String location = mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Mercado","amount":89.90,"category":"FOOD","date":"2026-10-15"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Mercado"))
                .andExpect(jsonPath("$.amount").value(89.90))
                .andExpect(jsonPath("$.category").value("FOOD"));
    }

    @Test
    void returns404ProblemDetailForUnknownId() throws Exception {
        mockMvc.perform(get("/api/expenses/987654"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Expense not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void returns400WhenAmountIsNegative() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Teste","amount":-1,"category":"FOOD","date":"2026-10-15"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.amount").exists());
    }

    @Test
    void returns400WhenIdIsNotANumber() throws Exception {
        mockMvc.perform(get("/api/expenses/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void exposesHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void exposesOpenApiDocument() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Expense Tracker API")))
                .andExpect(jsonPath("$.paths['/api/expenses']").exists());
    }
}
