package com.allobankdev.splitbill.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.allobankdev.splitbill.entity.BillGroup;
import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.response.SettlementItem;
import com.allobankdev.splitbill.model.response.SettlementResponse;
import com.allobankdev.splitbill.repository.BillGroupRepository;

@Service
public class SettlementService {

    private final BillGroupRepository billGroupRepository;
    private final PersonalizationService personalizationService;

    public SettlementService(
            BillGroupRepository billGroupRepository,
            PersonalizationService personalizationService) {

        this.billGroupRepository = billGroupRepository;
        this.personalizationService = personalizationService;
    }

    public Response<Object> getSettlement(Integer groupId) {

        BillGroup group =
                billGroupRepository.findById(groupId)
                        .orElse(null);

        if (group == null) {

            return Response.create(
                    "STL",
                    Response.RESPONSE_CODE_NOT_FOUND,
                    "Group not found",
                    null
            );
        }

  

        List<SettlementItem> settlements =
                new ArrayList<>();

        BigDecimal totalExpense =
                BigDecimal.ZERO;

        BigDecimal serviceChargePct =
                personalizationService
                        .calculateServiceChargePercentage();

        BigDecimal serviceChargeAmount =
                personalizationService
                        .calculateServiceChargeAmount(
                                totalExpense
                        );

        SettlementResponse response =
                new SettlementResponse(
                        settlements,
                        serviceChargePct,
                        serviceChargeAmount
                );

        return Response.create(
                "STL",
                Response.RESPONSE_CODE_SUCCESS,
                "Settlement calculated successfully",
                response
        );
    }
}