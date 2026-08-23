package com.allobankdev.splitbill;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.response.SettlementItemRes;
import com.allobankdev.splitbill.services.SettlementCalculator;

class SettlementCalculatorTest {

    private SettlementCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new SettlementCalculator();
    }

    @Test
    void shouldCalculateSimpleSettlement() {

        Participant andi =
                new Participant(null, "Andi");

        Participant budi =
                new Participant(null, "Budi");

        Participant citra =
                new Participant(null, "Citra");


        Map<Integer, Participant> participants =
                new HashMap<>();

        participants.put(1, andi);
        participants.put(2, budi);
        participants.put(3, citra);

        Map<Integer, BigDecimal> paidMap =
                new HashMap<>();

        paidMap.put(
                1,
                new BigDecimal("300000")
        );

        Map<Integer, BigDecimal> shareMap =
                new HashMap<>();

        shareMap.put(
                1,
                new BigDecimal("100000")
        );

        shareMap.put(
                2,
                new BigDecimal("100000")
        );

        shareMap.put(
                3,
                new BigDecimal("100000")
        );

        List<SettlementItemRes> result =
                calculator.calculate(
                        participants,
                        paidMap,
                        shareMap
                );

        assertEquals(2, result.size());

        assertEquals(
                "Budi",
                result.get(0).from()
        );

        assertEquals(
                "Andi",
                result.get(0).to()
        );

        assertEquals(
                new BigDecimal("100000"),
                result.get(0).amount()
        );

        assertEquals(
                "Citra",
                result.get(1).from()
        );

        assertEquals(
                "Andi",
                result.get(1).to()
        );

        assertEquals(
                new BigDecimal("100000"),
                result.get(1).amount()
        );
    }
}