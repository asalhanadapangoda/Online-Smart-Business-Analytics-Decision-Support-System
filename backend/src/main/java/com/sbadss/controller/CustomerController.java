package com.sbadss.controller;

import com.sbadss.common.ApiResponse;
import com.sbadss.dto.CustomerRequest;
import com.sbadss.dto.CustomerResponse;
import com.sbadss.service.CustomerService;
import com.sbadss.util.ApiEndpoints;
import com.sbadss.util.CommonMessages;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ApiEndpoints.CUSTOMER_BASE)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getCustomers(@RequestParam(required = false) Long branchId) {
        log.info("REST request to get customers for branch: {}", branchId);
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomersByBranch(branchId), CommonMessages.CUSTOMERS_FETCH_SUCCESS));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(@Valid @RequestBody CustomerRequest dto) {
        log.info("REST request to create customer: {}", dto.getName());
        return ResponseEntity.ok(ApiResponse.success(customerService.createCustomer(dto), CommonMessages.CUSTOMER_CREATE_SUCCESS));
    }

    @GetMapping(ApiEndpoints.CUSTOMER_SEARCH)
    public ResponseEntity<ApiResponse<CustomerResponse>> searchByPhone(@RequestParam String phoneNumber) {
        log.info("REST request to search customer by phone: {}", phoneNumber);
        CustomerResponse customer = customerService.findByPhoneNumber(phoneNumber);
        if (customer == null) {
            return ResponseEntity.ok(ApiResponse.success(null, CommonMessages.CUSTOMER_NOT_FOUND));
        }
        return ResponseEntity.ok(ApiResponse.success(customer, CommonMessages.CUSTOMER_FOUND));
    }
}
