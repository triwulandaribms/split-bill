package com.allobankdev.splitbill.model.request;


import java.util.List;

public record BillGroupReq(
        String name,
        List<String> participants
) {
}