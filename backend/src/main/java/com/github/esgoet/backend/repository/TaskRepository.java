package com.github.esgoet.backend.repository;

import com.github.esgoet.backend.model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    Optional<List<Task>> findTasksByColumnId(String columnId);

    void deleteTasksByColumnId(String columnId);
}
