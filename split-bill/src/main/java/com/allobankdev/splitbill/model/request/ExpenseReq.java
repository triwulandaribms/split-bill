package com.allobankdev.splitbill.model.request;


import java.math.BigDecimal;
import java.util.List;

import com.allobankdev.splitbill.entity.Expense.ExpenseCategory;

public record ExpenseReq(
        Integer groupId,
        Integer paidBy,
        BigDecimal amount,
        String description,
        ExpenseCategory category,
        List<ExpenseParticipantReq> participants
) {
}