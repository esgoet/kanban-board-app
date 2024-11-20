package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.NewTaskDto;
import com.github.esgoet.backend.dto.TaskDto;
import com.github.esgoet.backend.exception.ElementNotFoundException;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Task;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final IdService idService;
    private final TaskRepository taskRepository;
    private final BoardRepository boardRepository;

    private static final String TASK_ELEMENT = "Task";
    private static final String BOARD_ELEMENT = "Board including column";

    public List<Task> getTasksByColumnId(String columnId) {
        return taskRepository.findTasksByColumnId(columnId)
                .orElseThrow(() -> new ElementNotFoundException("Tasks in column", columnId));
    }

    public Task getTaskById(String id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(TASK_ELEMENT, id));
    }

    public Task createTask(String columnId, NewTaskDto task) {
        String generatedId = idService.generateId();
        Board board = boardRepository.findByColumnId(columnId)
                .orElseThrow(() -> new ElementNotFoundException(BOARD_ELEMENT, columnId));
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
                task.deadline()));
    }

    public Task updateTask(String id, TaskDto taskDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(TASK_ELEMENT, id));
        if (!existingTask.columnId().equals(taskDto.columnId())) {
            Board board = boardRepository.findByColumnId(existingTask.columnId())
                    .orElseThrow(() -> new ElementNotFoundException(BOARD_ELEMENT, taskDto.columnId()));

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
                .withDeadline(taskDto.deadline()));
    }

    public void deleteTask(String id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(TASK_ELEMENT, id));
        Board board = boardRepository.findByColumnId(task.columnId())
                .orElseThrow(() -> new ElementNotFoundException(BOARD_ELEMENT, task.columnId()));
        board.columns().stream()
                .filter(column -> column.id().equals(task.columnId()))
                .findFirst()
                .ifPresent(column -> column.tasks().remove(id));
        boardRepository.save(board);
        taskRepository.deleteById(id);
    }
}
