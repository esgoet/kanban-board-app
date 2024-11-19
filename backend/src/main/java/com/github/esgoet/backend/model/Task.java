package com.github.esgoet.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("tasks")
public record Task(
        String id,
        String title,
        String description,
        String status,
        Instant deadline
) {
}
