package com.allobankdev.splitbill.model.response;

import java.math.BigDecimal;

public record PaymentRes(
        Integer fromParticipantId,
        Integer toParticipantId,
        BigDecimal amount,
        BigDecimal remainingAmount,
        PaymentStatus status) {
}