package com.allobankdev.splitbill.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.entity.Expense;
import com.allobankdev.splitbill.entity.ExpenseParticipant;
import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.entity.Payment;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.response.SettlementItemRes;
import com.allobankdev.splitbill.model.response.SettlementRes;
import com.allobankdev.splitbill.repository.BillGroupRepository;
import com.allobankdev.splitbill.repository.ExpenseParticipantRepository;
import com.allobankdev.splitbill.repository.ExpenseRepository;
import com.allobankdev.splitbill.repository.PaymentRepository;

@Service
public class SettlementService {

        private final BillGroupRepository billGroupRepository;
        private final ExpenseRepository expenseRepository;
        private final ExpenseParticipantRepository expenseParticipantRepository;
        private final PersonalizationService personalizationService;
        private final SettlementCalculator settlementCalculator;
        private final PaymentRepository paymentRepository;

        public SettlementService(
                        BillGroupRepository billGroupRepository,
                        ExpenseRepository expenseRepository,
                        ExpenseParticipantRepository expenseParticipantRepository,
                        PersonalizationService personalizationService,
                        SettlementCalculator settlementCalculator,
                        PaymentRepository paymentRepository) {

                this.billGroupRepository = billGroupRepository;
                this.expenseRepository = expenseRepository;
                this.expenseParticipantRepository = expenseParticipantRepository;
                this.personalizationService = personalizationService;
                this.settlementCalculator = settlementCalculator;
                this.paymentRepository = paymentRepository;
        }

        public Response<Object> getSettlement(Integer groupId) {

                BillGroup group = billGroupRepository
                                .findById(groupId)
                                .orElse(null);

                if (group == null) {
                        return Response.create(
                                        "STL",
                                        Response.RESPONSE_CODE_NOT_FOUND,
                                        "Group not found",
                                        null);
                }

                List<Expense> expenses = expenseRepository.findByGroupId(groupId);

                Map<Integer, Participant> participantMap = new HashMap<>();

                Map<Integer, BigDecimal> paidMap = new HashMap<>();

                Map<Integer, BigDecimal> shareMap = new HashMap<>();

                BigDecimal totalExpense = BigDecimal.ZERO;

                for (Expense expense : expenses) {

                        totalExpense = totalExpense.add(
                                        expense.getAmount());

                        Participant payer = expense.getPaidBy();

                        Integer payerId = payer.getId();

                        participantMap.put(
                                        payerId,
                                        payer);

                        paidMap.put(
                                        payerId,
                                        paidMap.getOrDefault(
                                                        payerId,
                                                        BigDecimal.ZERO)
                                                        .add(expense.getAmount()));

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
                                                                BigDecimal.ZERO)
                                                                .add(
                                                                                expenseParticipant
                                                                                                .getShareAmount()));
                        }
                }

                List<SettlementItemRes> settlements = settlementCalculator.calculate(
                                participantMap,
                                paidMap,
                                shareMap);

               
                List<Payment> payments = paymentRepository.findByGroupId(groupId);

             
                settlements = applyPayments(
                                settlements,
                                payments);

                BigDecimal serviceChargePct = personalizationService
                                .calculateServiceChargePercentage();

                BigDecimal serviceChargeAmount = personalizationService
                                .calculateServiceChargeAmount(
                                                totalExpense);

                SettlementRes response = new SettlementRes(
                                settlements,
                                serviceChargePct,
                                serviceChargeAmount);

                return Response.create(
                                "STL",
                                Response.RESPONSE_CODE_SUCCESS,
                                "Settlement calculated successfully",
                                response);
        }

        private List<SettlementItemRes> applyPayments(
                        List<SettlementItemRes> settlements,
                        List<Payment> payments) {

                List<SettlementItemRes> result = new ArrayList<>();

                for (SettlementItemRes settlement : settlements) {

                        BigDecimal paidAmount = payments.stream()
                                        .filter(payment -> payment.getFromParticipant()
                                                        .getId()
                                                        .equals(
                                                                        settlement
                                                                                        .fromParticipantId())
                                                        &&
                                                        payment.getToParticipant()
                                                                        .getId()
                                                                        .equals(
                                                                                        settlement
                                                                                                        .toParticipantId()))
                                        .map(Payment::getAmount)
                                        .reduce(
                                                        BigDecimal.ZERO,
                                                        BigDecimal::add);

                        BigDecimal remaining = settlement.amount()
                                        .subtract(paidAmount);

                        if (remaining.compareTo(
                                        BigDecimal.ZERO) > 0) {

                                result.add(
                                                new SettlementItemRes(
                                                                settlement.fromParticipantId(),
                                                                settlement.toParticipantId(),
                                                                settlement.from(),
                                                                settlement.to(),
                                                                remaining));
                        }
                }

                return result;
        }
}