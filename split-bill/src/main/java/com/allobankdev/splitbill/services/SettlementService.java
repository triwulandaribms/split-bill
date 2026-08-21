package com.allobankdev.splitbill.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.entity.Expense;
import com.allobankdev.splitbill.entity.ExpenseParticipant;
import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.response.SettlementItem;
import com.allobankdev.splitbill.model.response.SettlementResponse;
import com.allobankdev.splitbill.repository.BillGroupRepository;
import com.allobankdev.splitbill.repository.ExpenseParticipantRepository;
import com.allobankdev.splitbill.repository.ExpenseRepository;

@Service
public class SettlementService {

        private final BillGroupRepository billGroupRepository;
        private final ExpenseRepository expenseRepository;
        private final ExpenseParticipantRepository expenseParticipantRepository;
        private final PersonalizationService personalizationService;

        public SettlementService(
                        BillGroupRepository billGroupRepository,
                        ExpenseRepository expenseRepository,
                        ExpenseParticipantRepository expenseParticipantRepository,
                        PersonalizationService personalizationService) {

                this.billGroupRepository = billGroupRepository;
                this.expenseRepository = expenseRepository;
                this.expenseParticipantRepository = expenseParticipantRepository;
                this.personalizationService = personalizationService;
        }

        public Response<Object> getSettlement(Integer groupId) {

                BillGroup group = billGroupRepository.findById(groupId)
                                .orElse(null);

                if (group == null) {

                        return Response.create(
                                        "STL",
                                        Response.RESPONSE_CODE_NOT_FOUND,
                                        "Group not found",
                                        null);
                }

                List<Expense> expenses = expenseRepository.findByGroupId(groupId);

                Map<Integer, BigDecimal> paidMap = new HashMap<>();

                Map<Integer, BigDecimal> shareMap = new HashMap<>();

                Map<Integer, Participant> participantMap = new HashMap<>();

                BigDecimal totalExpense = BigDecimal.ZERO;

                for (Expense expense : expenses) {

                        totalExpense = totalExpense.add(expense.getAmount());

                        Participant payer = expense.getPaidBy();

                        Integer payerId = payer.getId();

                        participantMap.put(
                                        payerId,
                                        payer);

                        paidMap.put(
                                        payerId,
                                        paidMap.getOrDefault(
                                                        payerId,
                                                        BigDecimal.ZERO).add(expense.getAmount()));

                        List<ExpenseParticipant> expenseParticipants = expenseParticipantRepository
                                        .findByExpenseId(
                                                        expense.getId());

                        for (ExpenseParticipant expenseParticipant : expenseParticipants) {

                                Participant participant = expenseParticipant.getParticipant();

                                Integer participantId = participant.getId();

                                participantMap.put(
                                                participantId,
                                                participant);

                                shareMap.put(
                                                participantId,
                                                shareMap.getOrDefault(
                                                                participantId,
                                                                BigDecimal.ZERO).add(
                                                                                expenseParticipant.getShareAmount()));
                        }
                }

                Map<Integer, BigDecimal> balanceMap = new HashMap<>();

                for (Integer participantId : participantMap.keySet()) {

                        BigDecimal paid = paidMap.getOrDefault(
                                        participantId,
                                        BigDecimal.ZERO);

                        BigDecimal share = shareMap.getOrDefault(
                                        participantId,
                                        BigDecimal.ZERO);

                        BigDecimal balance = paid.subtract(share);

                        balanceMap.put(
                                        participantId,
                                        balance);
                }

                List<Balance> creditors = new ArrayList<>();

                List<Balance> debtors = new ArrayList<>();

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

                creditors.sort(
                                Comparator.comparing(
                                                Balance::amount).reversed());

                debtors.sort(
                                Comparator.comparing(
                                                Balance::amount).reversed());

                List<SettlementItem> settlements = new ArrayList<>();

                int debtorIndex = 0;
                int creditorIndex = 0;

                while (debtorIndex < debtors.size() && creditorIndex < creditors.size()) {

                        Balance debtor = debtors.get(debtorIndex);

                        Balance creditor = creditors.get(creditorIndex);

                        BigDecimal settlementAmount = debtor.amount().min(
                                        creditor.amount());

                        Participant debtorParticipant = participantMap.get(
                                        debtor.participantId());

                        Participant creditorParticipant = participantMap.get(
                                        creditor.participantId());

                        settlements.add(
                                        new SettlementItem(
                                                        debtorParticipant.getName(),
                                                        creditorParticipant.getName(),
                                                        settlementAmount));

                        BigDecimal remainingDebt = debtor.amount()
                                        .subtract(
                                                        settlementAmount);

                        BigDecimal remainingCredit = creditor.amount()
                                        .subtract(
                                                        settlementAmount);

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

                BigDecimal serviceChargePct = personalizationService
                                .calculateServiceChargePercentage();

                BigDecimal serviceChargeAmount = personalizationService
                                .calculateServiceChargeAmount(
                                                totalExpense);

                SettlementResponse response = new SettlementResponse(
                                settlements,
                                serviceChargePct,
                                serviceChargeAmount);

                return Response.create(
                                "STL",
                                Response.RESPONSE_CODE_SUCCESS,
                                "Settlement calculated successfully",
                                response);
        }

        private record Balance(
                        Integer participantId,
                        BigDecimal amount) {
        }
}