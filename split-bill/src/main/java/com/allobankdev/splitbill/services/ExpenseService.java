package com.allobankdev.splitbill.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.entity.Expense;
import com.allobankdev.splitbill.entity.ExpenseParticipant;
import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.ExpenseParticipantReq;
import com.allobankdev.splitbill.model.request.ExpenseReq;
import com.allobankdev.splitbill.model.response.ExpenseDto;
import com.allobankdev.splitbill.repository.BillGroupRepository;
import com.allobankdev.splitbill.repository.ExpenseParticipantRepository;
import com.allobankdev.splitbill.repository.ExpenseRepository;
import com.allobankdev.splitbill.repository.ParticipantRepository;

@Service
public class ExpenseService {

        private final ExpenseRepository expenseRepository;
        private final ExpenseParticipantRepository expenseParticipantRepository;
        private final BillGroupRepository billGroupRepository;
        private final ParticipantRepository participantRepository;

        public ExpenseService(
                        ExpenseRepository expenseRepository,
                        ExpenseParticipantRepository expenseParticipantRepository,
                        BillGroupRepository billGroupRepository,
                        ParticipantRepository participantRepository) {

                this.expenseRepository = expenseRepository;
                this.expenseParticipantRepository = expenseParticipantRepository;
                this.billGroupRepository = billGroupRepository;
                this.participantRepository = participantRepository;
        }

        @Transactional
        public Response<Object> createExpense(ExpenseReq req) {

                if (req.amount() == null ||
                                req.amount().compareTo(BigDecimal.ZERO) <= 0) {

                        return Response.create(
                                        "EXP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Amount must be greater than zero",
                                        null);
                }

                if (req.groupId() == null ||
                                req.paidBy() == null ||
                                req.participants() == null ||
                                req.participants().isEmpty()) {

                        return Response.create(
                                        "EXP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Invalid expense data",
                                        null);
                }

                BillGroup group = billGroupRepository.findById(req.groupId())
                                .orElse(null);

                if (group == null) {
                        return Response.create(
                                        "EXP",
                                        Response.RESPONSE_CODE_NOT_FOUND,
                                        "Group not found",
                                        null);
                }

                Participant payer = participantRepository.findById(req.paidBy())
                                .orElse(null);

                if (payer == null ||
                                !payer.getGroup().getId().equals(group.getId())) {

                        return Response.create(
                                        "EXP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Payer is not a participant of this group",
                                        null);
                }

                Set<Integer> participantIds = new HashSet<>();

                BigDecimal totalShare = BigDecimal.ZERO;

                List<Participant> participants = new ArrayList<>();

                for (ExpenseParticipantReq participantReq : req.participants()) {

                        if (participantReq.participantId() == null ||
                                        participantReq.shareAmount() == null ||
                                        participantReq.shareAmount()
                                                        .compareTo(BigDecimal.ZERO) <= 0) {

                                return Response.create(
                                                "EXP",
                                                Response.RESPONSE_CODE_INVALID_INPUT,
                                                "Participant ID and positive share amount are required",
                                                null);
                        }

                        if (!participantIds.add(
                                        participantReq.participantId())) {

                                return Response.create(
                                                "EXP",
                                                Response.RESPONSE_CODE_INVALID_INPUT,
                                                "Duplicate participant in expense",
                                                null);
                        }

                        Participant participant = participantRepository
                                        .findById(
                                                        participantReq.participantId())
                                        .orElse(null);

                        if (participant == null) {
                                return Response.create(
                                                "EXP",
                                                Response.RESPONSE_CODE_NOT_FOUND,
                                                "Participant not found",
                                                null);
                        }

                        if (!participant.getGroup()
                                        .getId()
                                        .equals(group.getId())) {

                                return Response.create(
                                                "EXP",
                                                Response.RESPONSE_CODE_INVALID_INPUT,
                                                "Participant does not belong to this group",
                                                null);
                        }

                        participants.add(participant);

                        totalShare = totalShare.add(
                                        participantReq.shareAmount());
                }

                if (totalShare.compareTo(req.amount()) != 0) {

                        return Response.create(
                                        "EXP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Total participant shares must equal expense amount",
                                        null);
                }

                Expense expense = new Expense(
                                group,
                                payer,
                                req.amount(),
                                req.description());

                expense = expenseRepository.save(expense);

                List<ExpenseParticipant> expenseParticipants = new ArrayList<>();

                for (int i = 0; i < req.participants().size(); i++) {

                        ExpenseParticipantReq participantReq = req.participants().get(i);

                        Participant participant = participants.get(i);

                        ExpenseParticipant expenseParticipant = new ExpenseParticipant(
                                        expense,
                                        participant,
                                        participantReq.shareAmount());

                        expenseParticipants.add(expenseParticipant);
                }

                expenseParticipantRepository.saveAll(
                                expenseParticipants);

                ExpenseDto response = new ExpenseDto(
                                expense.getId(),
                                group.getId(),
                                payer.getId(),
                                expense.getAmount(),
                                expense.getDescription(),
                                expense.getCreatedAt());

                return Response.create(
                                "EXP",
                                Response.RESPONSE_CODE_SUCCESS,
                                "Expense created successfully",
                                response);
        }
}