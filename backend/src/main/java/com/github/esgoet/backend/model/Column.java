package com.github.esgoet.backend.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("columns")
public record Column(
        String id,
        String name,
        List<Task> tasks
) {
}
