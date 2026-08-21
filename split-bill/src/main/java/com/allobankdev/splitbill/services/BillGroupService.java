package com.allobankdev.splitbill.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.entity.Participant;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.BillGroupReq;
import com.allobankdev.splitbill.model.response.BillGroupDto;
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

        if (req.name() == null || req.name().isBlank()) {
            return Response.create(
                    "GRP",
                    Response.RESPONSE_CODE_INVALID_INPUT,
                    "Group name is required",
                    null
            );
        }

        if (req.participants() == null ||
                req.participants().isEmpty()) {

            return Response.create(
                    "GRP",
                    Response.RESPONSE_CODE_INVALID_INPUT,
                    "Participants are required",
                    null
            );
        }

        BillGroup group =
                new BillGroup(req.name());

        group = billGroupRepository.save(group);

        List<Participant> participants =
                new ArrayList<>();

        for (String name : req.participants()) {

            if (name == null || name.isBlank()) {
                return Response.create(
                        "GRP",
                        Response.RESPONSE_CODE_INVALID_INPUT,
                        "Participant name cannot be empty",
                        null
                );
            }

            Participant participant =
                    new Participant(group, name);

            participants.add(participant);
        }

        participantRepository.saveAll(participants);

        BillGroupDto response =
                new BillGroupDto(
                        group.getId(),
                        group.getName(),
                        group.getCreatedAt(),
                        group.getUpdatedAt()
                );

        return Response.create(
                "GRP",
                Response.RESPONSE_CODE_SUCCESS,
                "Group created successfully",
                response
        );
    }

    public Response<Object> getGroupById(Integer id) {

        return billGroupRepository.findById(id)
                .map(group -> {

                    BillGroupDto response =
                            new BillGroupDto(
                                    group.getId(),
                                    group.getName(),
                                    group.getCreatedAt(),
                                    group.getUpdatedAt()
                            );

                    return Response.create(
                            "GRP",
                            Response.RESPONSE_CODE_SUCCESS,
                            "Group found",
                            response
                    );
                })
                .orElse(
                        Response.create(
                                "GRP",
                                Response.RESPONSE_CODE_NOT_FOUND,
                                "Group not found",
                                null
                        )
                );
    }
}