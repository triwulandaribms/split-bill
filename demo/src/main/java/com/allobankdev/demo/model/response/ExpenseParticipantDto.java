package com.allobankdev.demo.model.response;

import java.math.BigDecimal;

public record ExpenseParticipantDto(
        Integer id,
        Integer expenseId,
        Integer participantId,
        BigDecimal shareAmount
) {
}