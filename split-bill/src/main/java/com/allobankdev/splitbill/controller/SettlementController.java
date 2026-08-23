package com.allobankdev.splitbill.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.services.SettlementService;

@RestController
@RequestMapping("/api/groups")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(
            SettlementService settlementService) {

        this.settlementService = settlementService;
    }

    @GetMapping("/settlement/{groupId}")
    public ResponseEntity<Response<Object>> getSettlement(
            @PathVariable Integer groupId) {

        Response<Object> response =
                settlementService.getSettlement(groupId);

        if (response.code().equals(
                "STL" + Response.RESPONSE_CODE_NOT_FOUND)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}