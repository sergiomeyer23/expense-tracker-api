package com.sergiomeyer.expensetracker.dto;

import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponse(
        Long id,
        String description,
        BigDecimal amount,
        Category category,
        LocalDate date
) {

    public static ExpenseResponse from(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getDescription(),
                expense.getAmount(),
                expense.getCategory(),
                expense.getDate());
    }
}
