package com.allobankdev.splitbill.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.allobankdev.splitbill.entity.Expense.ExpenseCategory;

public record ExpenseRes(
        Integer id,
        Integer groupId,
        Integer paidBy,
        BigDecimal amount,
        String description,
        ExpenseCategory category,
        LocalDateTime createdAt
) {
}