package com.allobankdev.splitbill.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.BillGroupReq;
import com.allobankdev.splitbill.model.response.BillGroupRes;
import com.allobankdev.splitbill.repository.BillGroupRepository;
import com.allobankdev.splitbill.repository.ParticipantRepository;

@Service
public class BillGroupService {

        private final BillGroupRepository billGroupRepository;
        private final ParticipantRepository participantRepository;

        public BillGroupService(
                        BillGroupRepository billGroupRepository,
                        ParticipantRepository participantRepository) {

                this.billGroupRepository = billGroupRepository;
                this.participantRepository = participantRepository;
        }

        @Transactional
        public Response<Object> createGroup(BillGroupReq req) {

                if (req == null) {
                        return Response.create(
                                        "GRP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Request required",
                                        null);
                }

                if (req.name() == null || req.name().isBlank()) {
                        return Response.create(
                                        "GRP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Group name required",
                                        null);
                }

                if (req.participants() == null ||
                                req.participants().isEmpty()) {

                        return Response.create(
                                        "GRP",
                                        Response.RESPONSE_CODE_INVALID_INPUT,
                                        "Participant required",
                                        null);
                }

                Set<String> participantNames = new HashSet<>();

                for (String name : req.participants()) {

                        if (name == null || name.isBlank()) {
                                return Response.create(
                                                "GRP",
                                                Response.RESPONSE_CODE_INVALID_INPUT,
                                                "Participant name required",
                                                null);
                        }

                        String normalizedName = name.trim().toLowerCase(Locale.ROOT);

                        if (!participantNames.add(normalizedName)) {
                                return Response.create(
                                                "GRP",
                                                Response.RESPONSE_CODE_INVALID_INPUT,
                                                "Duplicate participant: " + name,
                                                null);
                        }
                }

                BillGroup group = new BillGroup(req.name().trim());

                group = billGroupRepository.save(group);

                List<Participant> participants = new ArrayList<>();

                for (String name : req.participants()) {

                        Participant participant = new Participant(
                                        group,
                                        name.trim());

                        participants.add(participant);
                }

                participantRepository.saveAll(participants);

                BillGroupRes response = new BillGroupRes(
                                group.getId(),
                                group.getName(),
                                group.getCreatedAt(),
                                group.getUpdatedAt());

                return Response.create(
                                "GRP",
                                Response.RESPONSE_CODE_SUCCESS,
                                "Group created successfully",
                                response);
        }

        public Response<Object> getGroupById(Integer id) {

                return billGroupRepository.findById(id)
                                .map(group -> {

                                        BillGroupRes response = new BillGroupRes(
                                                        group.getId(),
                                                        group.getName(),
                                                        group.getCreatedAt(),
                                                        group.getUpdatedAt());

                                        return Response.create(
                                                        "GRP",
                                                        Response.RESPONSE_CODE_SUCCESS,
                                                        "Group found",
                                                        response);
                                })
                                .orElse(
                                                Response.create(
                                                                "GRP",
                                                                Response.RESPONSE_CODE_NOT_FOUND,
                                                                "Group not found",
                                                                null));
        }

}