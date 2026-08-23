package com.allobankdev.splitbill.model.response;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SettlementRes(

    List<SettlementItemRes> settlements,

    @JsonProperty("service_charge_pct")
    BigDecimal serviceChargePct,

    @JsonProperty("service_charge_amount")
    BigDecimal serviceChargeAmount
) {
}