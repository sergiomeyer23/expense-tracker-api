package com.sergiomeyer.expensetracker.controller;

import com.sergiomeyer.expensetracker.dto.ExpenseRequest;
import com.sergiomeyer.expensetracker.dto.ExpenseResponse;
import com.sergiomeyer.expensetracker.model.Category;
import com.sergiomeyer.expensetracker.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Expenses", description = "Create and query personal expenses")
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    @Operation(summary = "Create an expense",
            description = "Validates the body and stores a new expense. The Location header points to the created resource.")
    @ApiResponse(responseCode = "201", description = "Expense created")
    @ApiResponse(responseCode = "400", description = "Invalid body: the errors property lists each failing field",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody ExpenseRequest request) {
        ExpenseResponse created = service.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(summary = "Get an expense by id")
    @ApiResponse(responseCode = "200", description = "Expense found")
    @ApiResponse(responseCode = "400", description = "The id is not a valid number",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "No expense with this id",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping("/{id}")
    public ExpenseResponse findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "List expenses",
            description = "Returns expenses newest first, optionally filtered by category.")
    @ApiResponse(responseCode = "200", description = "List of expenses (empty when none match)")
    @ApiResponse(responseCode = "400", description = "Unknown category value",
            content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    @GetMapping
    public List<ExpenseResponse> findAll(@RequestParam(required = false) Category category) {
        return service.findAll(category);
    }
}
