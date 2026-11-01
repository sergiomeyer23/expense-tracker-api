package com.sergiomeyer.expensetracker.service;

import com.sergiomeyer.expensetracker.dto.ExpenseRequest;
import com.sergiomeyer.expensetracker.dto.ExpenseResponse;
import com.sergiomeyer.expensetracker.exception.ExpenseNotFoundException;
import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.model.Expense;
import com.sergiomeyer.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExpenseService {

    private final ExpenseRepository repository;

    public ExpenseService(ExpenseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ExpenseResponse create(ExpenseRequest request) {
        Expense expense = new Expense(
                request.description().trim(),
                request.amount(),
                request.category(),
                request.date());
        return ExpenseResponse.from(repository.save(expense));
    }

    @Transactional(readOnly = true)
    public ExpenseResponse findById(Long id) {
        return repository.findById(id)
                .map(ExpenseResponse::from)
                .orElseThrow(() -> new ExpenseNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> findAll(Category category) {
        List<Expense> expenses = category == null
                ? repository.findAllByOrderByDateDesc()
                : repository.findByCategoryOrderByDateDesc(category);
        return expenses.stream().map(ExpenseResponse::from).toList();
    }
}
