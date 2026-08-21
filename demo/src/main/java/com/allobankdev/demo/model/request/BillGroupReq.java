package com.allobankdev.demo.model.request;


import java.util.List;

public record BillGroupReq(
        String name,
        List<String> participants
) {
}