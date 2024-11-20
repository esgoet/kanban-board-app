package com.github.esgoet.backend.dto;

import java.time.Instant;

public record TaskDto(
        String columnId,
        String title,
        String description,
        Instant deadline
) {
}
