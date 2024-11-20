package com.github.esgoet.backend.model;

import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("tasks")
@With
public record Task(
        String id,
        String columnId,
        String title,
        String description,
        Instant deadline
) {
}
