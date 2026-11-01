package com.sergiomeyer.expensetracker.controller;

import com.sergiomeyer.expensetracker.dto.ExpenseResponse;
import com.sergiomeyer.expensetracker.exception.ExpenseNotFoundException;
import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.service.ExpenseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ExpenseController.class)
class ExpenseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExpenseService service;

    @Test
    void createReturns201WithLocationAndBody() throws Exception {
        when(service.create(any())).thenReturn(new ExpenseResponse(
                1L, "Supermercado", new BigDecimal("150.75"), Category.FOOD, LocalDate.of(2026, 10, 15)));

        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"Supermercado","amount":150.75,"category":"FOOD","date":"2026-10-15"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/expenses/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Supermercado"))
                .andExpect(jsonPath("$.amount").value(150.75))
                .andExpect(jsonPath("$.category").value("FOOD"));
    }

    @Test
    void createReturns400WithFieldErrorsWhenInvalid() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"","amount":-5,"category":"FOOD","date":"2999-01-01"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.description").exists())
                .andExpect(jsonPath("$.errors.amount").exists())
                .andExpect(jsonPath("$.errors.date").exists());

        verify(service, never()).create(any());
    }

    @Test
    void createReturns400ForUnknownCategory() throws Exception {
        mockMvc.perform(post("/api/expenses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description":"X","amount":10,"category":"NOPE","date":"2026-10-15"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findByIdReturns404WhenMissing() throws Exception {
        when(service.findById(99L)).thenThrow(new ExpenseNotFoundException(99L));

        mockMvc.perform(get("/api/expenses/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Expense not found with id 99"));
    }

    @Test
    void findAllFiltersByCategory() throws Exception {
        when(service.findAll(Category.TRANSPORT)).thenReturn(List.of(new ExpenseResponse(
                2L, "Uber", new BigDecimal("25.00"), Category.TRANSPORT, LocalDate.of(2026, 10, 20))));

        mockMvc.perform(get("/api/expenses").param("category", "TRANSPORT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Uber"));
    }
}
