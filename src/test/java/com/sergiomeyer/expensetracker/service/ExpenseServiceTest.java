package com.sergiomeyer.expensetracker.service;

import com.sergiomeyer.expensetracker.dto.ExpenseRequest;
import com.sergiomeyer.expensetracker.dto.ExpenseResponse;
import com.sergiomeyer.expensetracker.exception.ExpenseNotFoundException;
import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.model.Expense;
import com.sergiomeyer.expensetracker.repository.ExpenseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository repository;

    @InjectMocks
    private ExpenseService service;

    @Test
    void createTrimsDescriptionAndReturnsResponse() {
        when(repository.save(any(Expense.class))).thenAnswer(inv -> inv.getArgument(0));

        ExpenseResponse response = service.create(new ExpenseRequest(
                "  Cinema  ", new BigDecimal("40.00"), Category.LEISURE, LocalDate.of(2026, 10, 5)));

        assertThat(response.description()).isEqualTo("Cinema");
        assertThat(response.category()).isEqualTo(Category.LEISURE);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        when(repository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(7L))
                .isInstanceOf(ExpenseNotFoundException.class)
                .hasMessage("Expense not found with id 7");
    }
}
