package com.allobankdev.splitbill.model.response;


import java.time.LocalDateTime;

public record BillGroupDto(
        Integer id,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}