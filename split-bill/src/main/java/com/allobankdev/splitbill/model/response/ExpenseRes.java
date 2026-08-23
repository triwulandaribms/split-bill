package com.allobankdev.splitbill.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ExpenseRes(
        Integer id,
        Integer groupId,
        Integer paidBy,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
}