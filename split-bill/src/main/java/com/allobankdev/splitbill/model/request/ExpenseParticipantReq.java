package com.allobankdev.splitbill.model.request;

import java.math.BigDecimal;

public record ExpenseParticipantReq(
        Integer participantId,
        BigDecimal shareAmount
) {
}