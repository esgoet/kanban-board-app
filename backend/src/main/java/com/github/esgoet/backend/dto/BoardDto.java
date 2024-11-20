package com.github.esgoet.backend.dto;

import com.github.esgoet.backend.model.Column;

import java.util.List;

public record BoardDto(
        String name,
        List<Column> columns
) {
}
