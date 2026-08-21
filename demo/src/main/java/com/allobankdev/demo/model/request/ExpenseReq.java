package com.allobankdev.demo.model.request;


import java.math.BigDecimal;
import java.util.List;

public record ExpenseReq(
        Integer groupId,
        Integer paidBy,
        BigDecimal amount,
        String description,
        List<ExpenseParticipantReq> participants
) {
}