package com.allobankdev.demo.model.response;


import java.time.LocalDateTime;

public record ParticipantDto(
        Integer id,
        Integer groupId,
        String name,
        LocalDateTime createdAt
) {
}