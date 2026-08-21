package com.allobankdev.splitbill.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.allobankdev.splitbill.model.Response;
import com.allobankdev.splitbill.model.request.BillGroupReq;
import com.allobankdev.splitbill.services.BillGroupService;

@RestController
@RequestMapping("/api/groups")
public class BillGroupController {

    private final BillGroupService billGroupService;

    public BillGroupController(BillGroupService billGroupService) {
        this.billGroupService = billGroupService;
    }

    @PostMapping("/add")
    public ResponseEntity<Response<Object>> create(
            @RequestBody BillGroupReq req) {

        Response<Object> response =
                billGroupService.createGroup(req);

        if (response.code().equals(
                "GRP" + Response.RESPONSE_CODE_INVALID_INPUT)) {

            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<Object>> getById(
            @PathVariable Integer id) {

        Response<Object> response =
                billGroupService.getGroupById(id);

        if (response.code().equals(
                "GRP" + Response.RESPONSE_CODE_NOT_FOUND)) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(response);
        }

        return ResponseEntity.ok(response);
    }
}