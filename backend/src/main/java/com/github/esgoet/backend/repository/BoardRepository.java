package com.github.esgoet.backend.repository;

import com.github.esgoet.backend.model.Board;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface BoardRepository extends MongoRepository<Board, String> {

    @Query("{ 'columns.id': ?0 }")
    Optional<Board> findByColumnId(String columnId);
}
