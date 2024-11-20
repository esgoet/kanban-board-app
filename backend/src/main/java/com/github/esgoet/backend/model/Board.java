package com.github.esgoet.backend.model;

import lombok.With;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("boards")
@With
public record Board(
      String id,
      String name,
      List<Column> columns
) {
}
