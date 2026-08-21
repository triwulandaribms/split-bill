package com.allobankdev.demo.model.request;

import java.math.BigDecimal;

public record ExpenseParticipantReq(
        Integer participantId,
        BigDecimal shareAmount
) {
}