package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.entity.ExpenseCategory;
import com.sbadss.repository.ExpenseCategoryRepository;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.EXPENSE_CATEGORY_BASE)
@RequiredArgsConstructor
public class ExpenseCategoryController {

    private final ExpenseCategoryRepository categoryRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ExpenseCategory>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.findAll(), CommonMessages.EXPENSE_CATEGORIES_FETCH_SUCCESS));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<ApiResponse<ExpenseCategory>> createCategory(@RequestBody ExpenseCategory category) {
        return ResponseEntity.ok(ApiResponse.success(categoryRepository.save(category), CommonMessages.EXPENSE_CATEGORY_CREATE_SUCCESS));
    }
}
