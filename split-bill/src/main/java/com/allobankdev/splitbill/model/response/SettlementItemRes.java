package com.allobankdev.splitbill.model.response;


import java.math.BigDecimal;

public record SettlementItemRes(
        Integer fromParticipantId,
        Integer toParticipantId,
        String from,
        String to,
        BigDecimal amount
) {
}