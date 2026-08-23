package com.allobankdev.splitbill.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.response.SettlementItemRes;

@Component
public class SettlementCalculator {

        private record Balance(
                        Integer participantId,
                        BigDecimal amount) {
        }

        public List<SettlementItemRes> calculate(
                        Map<Integer, Participant> participants,
                        Map<Integer, BigDecimal> paidMap,
                        Map<Integer, BigDecimal> shareMap) {

                Map<Integer, BigDecimal> balanceMap = calculateBalances(
                                participants,
                                paidMap,
                                shareMap);

                List<Balance> creditors = new ArrayList<>();
                List<Balance> debtors = new ArrayList<>();

                separateBalances(
                                balanceMap,
                                creditors,
                                debtors);

                creditors.sort(
                                Comparator.comparing(Balance::amount).reversed());

                debtors.sort(
                                Comparator.comparing(Balance::amount).reversed());

                return createSettlements(
                                participants,
                                creditors,
                                debtors);
        }

        private Map<Integer, BigDecimal> calculateBalances(
                        Map<Integer, Participant> participants,
                        Map<Integer, BigDecimal> paidMap,
                        Map<Integer, BigDecimal> shareMap) {

                Map<Integer, BigDecimal> balanceMap = new HashMap<>();

                for (Integer participantId : participants.keySet()) {

                        BigDecimal paid = paidMap.getOrDefault(
                                        participantId,
                                        BigDecimal.ZERO);

                        BigDecimal share = shareMap.getOrDefault(
                                        participantId,
                                        BigDecimal.ZERO);

                        balanceMap.put(
                                        participantId,
                                        paid.subtract(share));
                }

                return balanceMap;
        }

        private void separateBalances(
                        Map<Integer, BigDecimal> balanceMap,
                        List<Balance> creditors,
                        List<Balance> debtors) {

                for (Map.Entry<Integer, BigDecimal> entry : balanceMap.entrySet()) {

                        BigDecimal balance = entry.getValue();

                        if (balance.compareTo(BigDecimal.ZERO) > 0) {

                                creditors.add(
                                                new Balance(
                                                                entry.getKey(),
                                                                balance));

                        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {

                                debtors.add(
                                                new Balance(
                                                                entry.getKey(),
                                                                balance.abs()));
                        }
                }
        }

        private List<SettlementItemRes> createSettlements(
                        Map<Integer, Participant> participants,
                        List<Balance> creditors,
                        List<Balance> debtors) {

                List<SettlementItemRes> settlements = new ArrayList<>();

                int debtorIndex = 0;
                int creditorIndex = 0;

                while (debtorIndex < debtors.size()
                                && creditorIndex < creditors.size()) {

                        Balance debtor = debtors.get(debtorIndex);
                        Balance creditor = creditors.get(creditorIndex);

                        BigDecimal settlementAmount = debtor.amount().min(creditor.amount());

                        Participant debtorParticipant = participants.get(debtor.participantId());

                        Participant creditorParticipant = participants.get(creditor.participantId());

                        settlements.add(
                                new SettlementItemRes(
                                        debtorParticipant.getId(),
                                        creditorParticipant.getId(),
                                        debtorParticipant.getName(),
                                        creditorParticipant.getName(),
                                        settlementAmount));

                        BigDecimal remainingDebt = debtor.amount()
                                        .subtract(settlementAmount);

                        BigDecimal remainingCredit = creditor.amount()
                                        .subtract(settlementAmount);

                        debtors.set(
                                        debtorIndex,
                                        new Balance(
                                                        debtor.participantId(),
                                                        remainingDebt));

                        creditors.set(
                                        creditorIndex,
                                        new Balance(
                                                        creditor.participantId(),
                                                        remainingCredit));

                        if (remainingDebt.compareTo(BigDecimal.ZERO) == 0) {
                                debtorIndex++;
                        }

                        if (remainingCredit.compareTo(BigDecimal.ZERO) == 0) {
                                creditorIndex++;
                        }
                }

                return settlements;
        }

}