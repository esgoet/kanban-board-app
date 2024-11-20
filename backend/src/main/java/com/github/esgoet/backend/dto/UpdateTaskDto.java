package com.github.esgoet.backend.dto;

import com.github.esgoet.backend.model.Status;

import java.time.Instant;

public record UpdateTaskDto(
        String columnId,
        String title,
        String description,
        Status status,
        Instant deadline
) {
}
