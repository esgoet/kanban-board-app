package com.github.esgoet.backend.controller;

import com.github.esgoet.backend.dto.TaskDto;
import com.github.esgoet.backend.dto.UpdateTaskDto;
import com.github.esgoet.backend.model.Task;
import com.github.esgoet.backend.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<Task>> getTasksByColumnId(@PathVariable String columnId) {
        List<Task> tasks = taskService.getTasksByColumnId(columnId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable String id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/column/{columnId}")
    public ResponseEntity<Task> createTask(@PathVariable String columnId, @RequestBody TaskDto taskDto) {
        Task task = taskService.createTask(columnId, taskDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody UpdateTaskDto taskDto) {
        Task task = taskService.updateTask(id, taskDto);
        return ResponseEntity.ok(task);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

}
