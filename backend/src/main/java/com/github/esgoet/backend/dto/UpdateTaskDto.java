package com.github.esgoet.backend.dto;

import java.time.Instant;

public record UpdateTaskDto(
        String columnId,
        String title,
        String description,
        Instant deadline
) {
}
