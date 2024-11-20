package com.github.esgoet.backend.dto;

import java.time.Instant;

public record TaskDto(
        String title,
        String description,
        Instant deadline
) {
}
