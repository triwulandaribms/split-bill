package com.allobankdev.splitbill.model.response;


import java.util.List;

public record BillGroupRes(
        Integer id,
        String name,
        List<String> participants
) {
}