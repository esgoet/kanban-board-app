package com.github.esgoet.backend.dto;

import java.time.Instant;

public record NewTaskDto(
        String title,
        String description,
        Instant deadline
) {
}
