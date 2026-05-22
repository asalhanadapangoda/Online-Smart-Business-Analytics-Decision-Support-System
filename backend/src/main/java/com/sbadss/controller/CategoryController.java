package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.entity.Category;
import com.sbadss.service.CategoryService;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiEndpoints.CATEGORY_BASE)
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.success(categoryService.getAllCategories(), CommonMessages.CATEGORIES_FETCH_SUCCESS));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(ApiResponse.success(categoryService.createCategory(category), CommonMessages.CATEGORY_CREATE_SUCCESS));
    }
}
