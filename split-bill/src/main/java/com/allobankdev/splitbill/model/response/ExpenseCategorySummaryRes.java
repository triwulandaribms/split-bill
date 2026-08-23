package com.allobankdev.splitbill.model.response;

import java.math.BigDecimal;

import com.allobankdev.splitbill.entity.Expense.ExpenseCategory;

public record ExpenseCategorySummaryRes(
        ExpenseCategory category,
        BigDecimal totalAmount
) {
}