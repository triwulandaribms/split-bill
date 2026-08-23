package com.allobankdev.splitbill.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.ExpenseReq;
import com.allobankdev.splitbill.services.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/add")
    public ResponseEntity<Response<Object>> create(
            @RequestBody ExpenseReq req) {

        Response<Object> response =
                expenseService.createExpense(req);

        if (response.code().equals(
                "EXP" + Response.RESPONSE_CODE_INVALID_INPUT)) {

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        if (response.code().equals(
                "EXP" + Response.RESPONSE_CODE_NOT_FOUND)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/summary/{groupId}")
    public ResponseEntity<Response<Object>> getCategorySummary(
            @PathVariable Integer groupId) {

        Response<Object> response =
                expenseService.getExpenseCategorySummary(groupId);

        if (response.code().equals(
                "EXP" + Response.RESPONSE_CODE_NOT_FOUND)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}