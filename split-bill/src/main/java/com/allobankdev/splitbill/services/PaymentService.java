package com.allobankdev.splitbill.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.entity.Payment;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.PaymentReq;
import com.allobankdev.splitbill.model.response.PaymentRes;
import com.allobankdev.splitbill.model.response.PaymentStatus;
import com.allobankdev.splitbill.repository.BillGroupRepository;
import com.allobankdev.splitbill.repository.ParticipantRepository;
import com.allobankdev.splitbill.repository.PaymentRepository;

@Service
public class PaymentService {

        private final PaymentRepository paymentRepository;
        private final BillGroupRepository billGroupRepository;
        private final ParticipantRepository participantRepository;
        private final SettlementService settlementService;

        public PaymentService(
                        PaymentRepository paymentRepository,
                        BillGroupRepository billGroupRepository,
                        ParticipantRepository participantRepository,
                        SettlementService settlementService) {

                this.paymentRepository = paymentRepository;
                this.billGroupRepository = billGroupRepository;
                this.participantRepository = participantRepository;
                this.settlementService = settlementService;
        }

        @Transactional
        public Response<Object> createPayment(
                        Integer groupId,
                        PaymentReq req) {

                if (req == null ||
                                req.fromParticipantId() == null ||
                                req.toParticipantId() == null ||
                                req.amount() == null) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Invalid payment data",
                                        null);
                }

                if (req.amount().compareTo(BigDecimal.ZERO) <= 0) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Payment amount must be greater than zero",
                                        null);
                }

                if (req.fromParticipantId()
                                .equals(req.toParticipantId())) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Payer and receiver must be different",
                                        null);
                }

                BillGroup group = billGroupRepository
                                .findById(groupId)
                                .orElse(null);

                if (group == null) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_NOT_FOUND,
                                        "Group not found",
                                        null);
                }

                Participant from = participantRepository
                                .findById(req.fromParticipantId())
                                .orElse(null);

                Participant to = participantRepository
                                .findById(req.toParticipantId())
                                .orElse(null);

                if (from == null || to == null) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_NOT_FOUND,
                                        "Participant not found",
                                        null);
                }

                if (!from.getGroup().getId().equals(groupId)
                                || !to.getGroup().getId().equals(groupId)) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Participants must belong to this group",
                                        null);
                }

                BigDecimal outstandingAmount = settlementService.getOutstandingAmount(
                                groupId,
                                req.fromParticipantId(),
                                req.toParticipantId());

                if (outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "No outstanding debt for this payment",
                                        null);
                }

                if (req.amount().compareTo(outstandingAmount) > 0) {

                        return Response.create(
                                        "PAY",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Payment amount exceeds outstanding debt",
                                        null);
                }

                Payment payment = new Payment(
                                group,
                                from,
                                to,
                                req.amount());

                paymentRepository.save(payment);

                BigDecimal remainingAmount = outstandingAmount.subtract(req.amount());

                PaymentStatus status = remainingAmount.compareTo(BigDecimal.ZERO) == 0
                                ? PaymentStatus.PAID
                                : PaymentStatus.PARTIALLY_PAID;

                PaymentRes response = new PaymentRes(
                                from.getId(),
                                to.getId(),
                                req.amount(),
                                remainingAmount,
                                status);

                return Response.create(
                                "PAY",
                                Response.RESPONSE_CODE_SUCCESS,
                                "Payment recorded successfully",
                                response);
        }
}