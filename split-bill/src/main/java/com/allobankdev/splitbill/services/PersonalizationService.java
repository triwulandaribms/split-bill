package com.allobankdev.splitbill.services;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PersonalizationService {

    private final String githubUsername;

    public PersonalizationService(
            @Value("${github.username}") String githubUsername) {

        this.githubUsername = githubUsername;
    }

    public BigDecimal calculateServiceChargePercentage() {

        int sum = githubUsername
                .toLowerCase()
                .chars()
                .sum();

        return BigDecimal.valueOf(sum % 10);
    }

    public BigDecimal calculateServiceChargeAmount(
            BigDecimal totalExpense) {

        BigDecimal percentage =
                calculateServiceChargePercentage();

        return totalExpense
                .multiply(percentage)
                .divide(BigDecimal.valueOf(100));
    }
}

