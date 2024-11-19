package com.github.esgoet.backend.repository;

import com.github.esgoet.backend.model.Board;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BoardRepository extends MongoRepository<Board, String> {
}
