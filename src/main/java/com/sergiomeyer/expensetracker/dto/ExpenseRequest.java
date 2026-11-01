package com.sergiomeyer.expensetracker.dto;

import com.sergiomeyer.expensetracker.model.Category;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseRequest(
        @NotBlank(message = "must not be blank")
        @Size(max = 255, message = "must have at most 255 characters")
        String description,

        @NotNull(message = "is required")
        @Positive(message = "must be greater than zero")
        @Digits(integer = 10, fraction = 2, message = "must have up to 10 integer digits and 2 decimal places")
        BigDecimal amount,

        @NotNull(message = "is required")
        Category category,

        @NotNull(message = "is required")
        @PastOrPresent(message = "must not be in the future")
        LocalDate date
) {
}
