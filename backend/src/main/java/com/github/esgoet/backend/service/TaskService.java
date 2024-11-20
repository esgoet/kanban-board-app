package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.TaskDto;
import com.github.esgoet.backend.dto.UpdateTaskDto;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Task;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final IdService idService;
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    public List<Task> getTasksByColumnId(String columnId) {
        return taskRepository.findTasksByColumnId(columnId)
                .orElseThrow(() -> new NoSuchElementException("Tasks with column ID " + columnId + " not found"));
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + id + " not found"));
    }

    public Task createTask(String columnId, TaskDto task) {
        String generatedId = idService.generateId();
        Board board = boardRepository.findByColumnId(columnId)
                .orElseThrow(() -> new NoSuchElementException("Board with column ID " + columnId + " not found"));
        board.columns().stream()
                .filter(column -> column.id().equals(columnId))
                .findFirst()
                .ifPresent(column -> column.tasks().add(generatedId));
        boardRepository.save(board);
        return taskRepository.save(new Task(
                generatedId,
                columnId,
                task.title(),
                task.description(),
                task.status(),
                task.deadline()));
    }

    public Task updateTask(String id, UpdateTaskDto taskDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + id + " not found"));
        if (!existingTask.columnId().equals(taskDto.columnId())) {
            Board board = boardRepository.findByColumnId(existingTask.columnId())
                    .orElseThrow(() -> new NoSuchElementException("Board with column ID " + existingTask.columnId() + " not found"));

            board.columns().stream()
                    .filter(column -> column.id().equals(existingTask.columnId()))
                    .findFirst()
                    .ifPresent(column -> column.tasks().remove(id));

            board.columns().stream()
                    .filter(column -> column.id().equals(taskDto.columnId()))
                    .findFirst()
                    .ifPresent(column -> column.tasks().add(id));

            boardRepository.save(board);
        }
        return taskRepository.save(existingTask
                .withColumnId(taskDto.columnId())
                .withTitle(taskDto.title())
                .withDescription(taskDto.description())
                .withStatus(taskDto.status())
                .withDeadline(taskDto.deadline()));
    }

    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Task with ID " + id + " not found"));
        Board board = boardRepository.findByColumnId(task.columnId())
                .orElseThrow(() -> new NoSuchElementException("Board with column ID " + task.columnId() + " not found"));
        board.columns().stream()
                .filter(column -> column.id().equals(task.columnId()))
                .findFirst()
                .ifPresent(column -> column.tasks().remove(id));
        boardRepository.save(board);
        taskRepository.deleteById(id);
    }
}
