package com.sergiomeyer.expensetracker.repository;

import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.model.Expense;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository repository;

    @Test
    void savesAndGeneratesId() {
        Expense saved = repository.save(
                new Expense("Supermercado", new BigDecimal("150.75"), Category.FOOD, LocalDate.of(2026, 10, 15)));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId()))
                .get()
                .satisfies(e -> {
                    assertThat(e.getDescription()).isEqualTo("Supermercado");
                    assertThat(e.getAmount()).isEqualByComparingTo("150.75");
                    assertThat(e.getCategory()).isEqualTo(Category.FOOD);
                });
    }

    @Test
    void findsByCategoryNewestFirst() {
        repository.save(new Expense("Onibus", new BigDecimal("5.00"), Category.TRANSPORT, LocalDate.of(2026, 10, 1)));
        repository.save(new Expense("Uber", new BigDecimal("25.00"), Category.TRANSPORT, LocalDate.of(2026, 10, 20)));
        repository.save(new Expense("Pizza", new BigDecimal("60.00"), Category.FOOD, LocalDate.of(2026, 10, 10)));

        List<Expense> result = repository.findByCategoryOrderByDateDesc(Category.TRANSPORT);

        assertThat(result).extracting(Expense::getDescription).containsExactly("Uber", "Onibus");
    }
}
