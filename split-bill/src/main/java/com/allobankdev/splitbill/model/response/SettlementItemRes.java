package com.allobankdev.splitbill.model.response;


import java.math.BigDecimal;

public record SettlementItemRes(
        String from,
        String to,
        BigDecimal amount
) {
}