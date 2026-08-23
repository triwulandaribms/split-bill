package com.allobankdev.splitbill.model.request;

import java.math.BigDecimal;

public record PaymentReq(
        Integer fromParticipantId,
        Integer toParticipantId,
        BigDecimal amount
) {
}