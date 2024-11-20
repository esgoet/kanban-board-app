package com.github.esgoet.backend.model;

import java.util.List;

public record Column(
        String id,
        String name,
        List<String> tasks
) {
}
