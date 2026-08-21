package com.allobankdev.splitbill.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.response.SettlementItem;

@Component
public class SettlementCalculator {

    public List<SettlementItem> calculate(
            Map<Integer, Participant> participants,
            Map<Integer, BigDecimal> paidMap,
            Map<Integer, BigDecimal> shareMap) {

        Map<Integer, BigDecimal> balanceMap =
                new HashMap<>();

        for (Integer participantId :
                participants.keySet()) {

            BigDecimal paid =
                    paidMap.getOrDefault(
                            participantId,
                            BigDecimal.ZERO
                    );

            BigDecimal share =
                    shareMap.getOrDefault(
                            participantId,
                            BigDecimal.ZERO
                    );

            BigDecimal balance =
                    paid.subtract(share);

            balanceMap.put(
                    participantId,
                    balance
            );
        }

        List<Balance> creditors =
                new ArrayList<>();

        List<Balance> debtors =
                new ArrayList<>();

        for (Map.Entry<Integer, BigDecimal> entry :
                balanceMap.entrySet()) {

            BigDecimal balance =
                    entry.getValue();

            if (balance.compareTo(
                    BigDecimal.ZERO) > 0) {

                creditors.add(
                        new Balance(
                                entry.getKey(),
                                balance
                        )
                );

            } else if (balance.compareTo(
                    BigDecimal.ZERO) < 0) {

                debtors.add(
                        new Balance(
                                entry.getKey(),
                                balance.abs()
                        )
                );
            }
        }

        creditors.sort(
                Comparator.comparing(
                        Balance::amount
                ).reversed()
        );

        debtors.sort(
                Comparator.comparing(
                        Balance::amount
                ).reversed()
        );

        List<SettlementItem> settlements =
                new ArrayList<>();

        int debtorIndex = 0;
        int creditorIndex = 0;

        while (debtorIndex < debtors.size()
                && creditorIndex < creditors.size()) {

            Balance debtor =
                    debtors.get(debtorIndex);

            Balance creditor =
                    creditors.get(creditorIndex);

            BigDecimal settlementAmount =
                    debtor.amount()
                            .min(creditor.amount());

            Participant debtorParticipant =
                    participants.get(
                            debtor.participantId()
                    );

            Participant creditorParticipant =
                    participants.get(
                            creditor.participantId()
                    );

            settlements.add(
                    new SettlementItem(
                            debtorParticipant.getName(),
                            creditorParticipant.getName(),
                            settlementAmount
                    )
            );

            BigDecimal remainingDebt =
                    debtor.amount()
                            .subtract(
                                    settlementAmount
                            );

            BigDecimal remainingCredit =
                    creditor.amount()
                            .subtract(
                                    settlementAmount
                            );

            debtors.set(
                    debtorIndex,
                    new Balance(
                            debtor.participantId(),
                            remainingDebt
                    )
            );

            creditors.set(
                    creditorIndex,
                    new Balance(
                            creditor.participantId(),
                            remainingCredit
                    )
            );

            if (remainingDebt.compareTo(
                    BigDecimal.ZERO) == 0) {

                debtorIndex++;
            }

            if (remainingCredit.compareTo(
                    BigDecimal.ZERO) == 0) {

                creditorIndex++;
            }
        }

        return settlements;
    }

    private record Balance(
            Integer participantId,
            BigDecimal amount) {
    }
}