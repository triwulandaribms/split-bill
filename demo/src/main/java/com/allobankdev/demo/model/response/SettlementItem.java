package com.allobankdev.demo.model.response;


import java.math.BigDecimal;

public record SettlementItem(
        String from,
        String to,
        BigDecimal amount
) {
}