package com.github.esgoet.backend.dto;

import com.github.esgoet.backend.model.Status;

import java.time.Instant;

public record TaskDto(
        String title,
        String description,
        Status status,
        Instant deadline
) {
}
