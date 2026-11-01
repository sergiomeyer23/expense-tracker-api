package com.sergiomeyer.expensetracker.repository;

import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByCategoryOrderByDateDesc(Category category);

    List<Expense> findAllByOrderByDateDesc();
}
