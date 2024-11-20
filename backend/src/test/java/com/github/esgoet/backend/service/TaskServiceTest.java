package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.TaskDto;
import com.github.esgoet.backend.dto.UpdateTaskDto;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.model.Status;
import com.github.esgoet.backend.model.Task;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    private final IdService idService = mock(IdService.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final TaskService taskService = new TaskService(idService, taskRepository, boardRepository);

    @Test
    void getTasksByColumnId_whenNoTasks_ReturnEmptyList() {
        //GIVEN
        String columnId = "col-1";
        when(taskRepository.findTasksByColumnId(columnId)).thenReturn(Optional.of(List.of()));
        //WHEN
        List<Task> actual = taskService.getTasksByColumnId(columnId);
        //THEN
        List<Task> expected = List.of();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getTasksByColumnId_whenTasksExist_ReturnTasks() {
        //GIVEN
        String columnId = "col-1";
        List<Task> tasks = List.of(
                new Task("task-1", columnId, "Task 1", "Description 1", Status.TODO, null),
                new Task("task-2", columnId, "Task 2", "Description 2", Status.IN_PROGRESS, null));
        when(taskRepository.findTasksByColumnId(columnId)).thenReturn(Optional.of(tasks));
        //WHEN
        List<Task> actual = taskService.getTasksByColumnId(columnId);
        //THEN
        List<Task> expected = List.of(
                new Task("task-1", columnId, "Task 1", "Description 1", Status.TODO, null),
                new Task("task-2", columnId, "Task 2", "Description 2", Status.IN_PROGRESS, null));
        verify(taskRepository).findTasksByColumnId(columnId);
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_whenTaskExists_ReturnTask() {
        //GIVEN
        Task task = new Task("task-1", "col-1", "Task 1", "Description 1", Status.TODO, null);
        when(taskRepository.findById("task-1")).thenReturn(Optional.of(task));
        //WHEN
        Task actual = taskService.getTaskById("task-1");
        //THEN
        Task expected = new Task("task-1", "col-1", "Task 1", "Description 1", Status.TODO, null);
        verify(taskRepository).findById("task-1");
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_whenNoTask_ThrowsNoSuchElementException() {
        //GIVEN
        String nonExistentId = "task-999";
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        //WHEN
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> taskService.getTaskById(nonExistentId));
        assertEquals("Task with ID task-999 not found", exception.getMessage());
    }

    @Test
    void createTask_savesTaskWithGeneratedId() {
        //GIVEN
        TaskDto taskDto = new TaskDto("Task 1", "Description 1", Status.TODO, null);
        String generatedId = "task-1";
        String columnId = "col-1";
        when(idService.generateId()).thenReturn(generatedId);

        Task savedTask = new Task(generatedId, columnId, taskDto.title(), taskDto.description(), taskDto.status(), taskDto.deadline());
        when(taskRepository.save(savedTask)).thenReturn(savedTask);

        Column column = new Column(columnId, "Column 1", new ArrayList<>());
        Board board = new Board("1", "Board 1", List.of(column));
        when(boardRepository.findByColumnId(columnId)).thenReturn(Optional.of(board));

        //WHEN
        Task actual = taskService.createTask(columnId, taskDto);
        //THEN
        Task expected = new Task(generatedId, columnId, taskDto.title(), taskDto.description(), taskDto.status(), taskDto.deadline());
        verify(idService).generateId();
        verify(boardRepository).findByColumnId(columnId);
        verify(boardRepository).save(board);
        verify(taskRepository).save(savedTask);
        assertEquals(expected, actual);
    }

    @Test
    void updateTask_whenTaskExistsWithSameColumnId_returnUpdatedTask() {
        //GIVEN
        String existingId = "task-1";
        Task existingTask = new Task(existingId, "col-1", "Task 1", "Description 1", Status.TODO, null);
        UpdateTaskDto updatedTaskDto = new UpdateTaskDto("col-1","Updated Task", "Updated Description", Status.DONE, null);

        when(taskRepository.findById(existingId)).thenReturn(Optional.of(existingTask));
        Task updatedTask = new Task(existingId, "col-1", updatedTaskDto.title(), updatedTaskDto.description(), updatedTaskDto.status(), updatedTaskDto.deadline());
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);
        //WHEN
        Task actual = taskService.updateTask(existingId, updatedTaskDto);
        //THEN
        Task expected = new Task(existingId, "col-1", updatedTaskDto.title(), updatedTaskDto.description(), updatedTaskDto.status(), updatedTaskDto.deadline());
        verify(taskRepository).findById(existingId);
        verify(boardRepository, never()).findByColumnId("col-1");
        verify(boardRepository, never()).save(any());
        verify(taskRepository).save(updatedTask);
        assertEquals(expected, actual);
    }

    @Test
    void updateTask_whenTaskExistsWithDifferentColumnId_returnUpdatedTask() {
        //GIVEN
        String existingId = "task-1";
        Task existingTask = new Task(existingId, "col-1", "Task 1", "Description 1", Status.TODO, null);
        UpdateTaskDto updatedTaskDto = new UpdateTaskDto("col-2","Updated Task", "Updated Description", Status.DONE, null);
        Task updatedTask = new Task(existingId, "col-2", updatedTaskDto.title(), updatedTaskDto.description(), updatedTaskDto.status(), updatedTaskDto.deadline());
        Column oldColumn = new Column("col-1", "Column 1", new ArrayList<>());
        oldColumn.tasks().add(existingId);
        Column newColumn = new Column("col-2", "Column 2", new ArrayList<>());
        Board board = new Board("1", "Board 1", List.of(oldColumn, newColumn));
        System.out.println(board);

        when(boardRepository.findByColumnId("col-1")).thenReturn(Optional.of(board));
        when(taskRepository.findById(existingId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);

        //WHEN
        Task actual = taskService.updateTask(existingId, updatedTaskDto);
        //THEN
        System.out.println(board);
        Task expected = new Task(existingId, "col-2", updatedTaskDto.title(), updatedTaskDto.description(), updatedTaskDto.status(), updatedTaskDto.deadline());
        verify(taskRepository).findById(existingId);
        verify(boardRepository).findByColumnId("col-1");
        verify(boardRepository).save(board);
        verify(taskRepository).save(updatedTask);
        assertEquals(expected, actual);
    }

    @Test
    void updateTask_whenTaskDoesNotExist_throwsNoSuchElementException() {
        //GIVEN
        String nonExistingId = "task-999";
        UpdateTaskDto updatedTaskDto = new UpdateTaskDto("col-1","Updated Task", "Updated Description", Status.DONE, null);

        when(taskRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        //THEN
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                //WHEN
                () -> taskService.updateTask(nonExistingId, updatedTaskDto));
        assertEquals("Task with ID task-999 not found", exception.getMessage());
    }

    @Test
    void deleteTask() {
        //GIVEN
        String existingId = "task-1";
        Task task = new Task(existingId, "col-1", "Task 1", "Description 1", Status.TODO, null);
        Column column = new Column("col-1", "Column 1", new ArrayList<>());
        column.tasks().add(existingId);
        Board board = new Board("1", "Board 1", List.of(column));

        when(taskRepository.findById(existingId)).thenReturn(Optional.of(task));
        when(boardRepository.findByColumnId("col-1")).thenReturn(Optional.of(board));

        doNothing().when(taskRepository).deleteById(existingId);
        //WHEN
        taskService.deleteTask(existingId);
        //THEN
        verify(taskRepository).findById(existingId);
        verify(boardRepository).findByColumnId("col-1");
        verify(boardRepository).save(board);
        verify(taskRepository).deleteById(existingId);
    }
}