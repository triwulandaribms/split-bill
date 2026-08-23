package com.allobankdev.splitbill.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.PaymentReq;
import com.allobankdev.splitbill.services.PaymentService;

@RestController
@RequestMapping("/api/groups")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping("/payments/{groupId}")
    public ResponseEntity<Response<Object>> createPayment(
            @PathVariable Integer groupId,
            @RequestBody PaymentReq req) {

        Response<Object> response =
                paymentService.createPayment(
                        groupId,
                        req);

        if (response.code().equals(
                "PAY" + Response.RESPONSE_CODE_INVALID_INPUT)) {

            return ResponseEntity
                    .badRequest()
                    .body(response);
        }

        if (response.code().equals(
                "PAY" + Response.RESPONSE_CODE_NOT_FOUND)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}