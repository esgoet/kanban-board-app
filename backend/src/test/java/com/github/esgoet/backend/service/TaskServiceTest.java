package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.NewTaskDto;
import com.github.esgoet.backend.dto.TaskDto;
import com.github.esgoet.backend.exception.ElementNotFoundException;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.model.Task;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {
    private final IdService idService = mock(IdService.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final TaskService taskService = new TaskService(idService, taskRepository, boardRepository);

    @Test
    void getTasks_whenNoTasks_ReturnEmptyList() {
        //GIVEN
        when(taskRepository.findAll()).thenReturn(List.of());
        //WHEN
        List<Task> actual = taskService.getAllTasks();
        //THEN
        List<Task> expected = List.of();
        verify(taskRepository).findAll();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getTasks_whenTasksExist_ReturnTasks() {
        //GIVEN
        String columnId = "col-1";
        List<Task> tasks = List.of(
                new Task("task-1", columnId, "Task 1", "Description 1", null),
                new Task("task-2", columnId, "Task 2", "Description 2", null));
        when(taskRepository.findAll()).thenReturn(tasks);
        //WHEN
        List<Task> actual = taskService.getAllTasks();
        //THEN
        List<Task> expected = List.of(
                new Task("task-1", columnId, "Task 1", "Description 1",  null),
                new Task("task-2", columnId, "Task 2", "Description 2",  null));
        verify(taskRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_whenTaskExists_ReturnTask() {
        //GIVEN
        Task task = new Task("task-1", "col-1", "Task 1", "Description 1", null);
        when(taskRepository.findById("task-1")).thenReturn(Optional.of(task));
        //WHEN
        Task actual = taskService.getTaskById("task-1");
        //THEN
        Task expected = new Task("task-1", "col-1", "Task 1", "Description 1", null);
        verify(taskRepository).findById("task-1");
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_whenNoTask_ThrowsElementNotFoundException() {
        //GIVEN
        String nonExistentId = "task-999";
        when(taskRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        //WHEN
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class, () -> taskService.getTaskById(nonExistentId));
        assertEquals("Task with ID task-999 not found", exception.getMessage());
    }

    @Test
    void createTask_savesTaskWithGeneratedId() {
        //GIVEN
        NewTaskDto taskDto = new NewTaskDto("Task 1", "Description 1",null);
        String generatedId = "task-1";
        String columnId = "col-1";
        when(idService.generateId()).thenReturn(generatedId);

        Task savedTask = new Task(generatedId, columnId, taskDto.title(), taskDto.description(), taskDto.deadline());
        when(taskRepository.save(savedTask)).thenReturn(savedTask);

        Column column = new Column(columnId, "Column 1", new ArrayList<>());
        Board board = new Board("1", "Board 1", List.of(column));
        when(boardRepository.findByColumnId(columnId)).thenReturn(Optional.of(board));

        //WHEN
        Task actual = taskService.createTask(columnId, taskDto);
        //THEN
        Task expected = new Task(generatedId, columnId, taskDto.title(), taskDto.description(),taskDto.deadline());
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
        Task existingTask = new Task(existingId, "col-1", "Task 1", "Description 1",null);
        TaskDto updatedTaskDto = new TaskDto("col-1","Updated Task", "Updated Description", null);

        when(taskRepository.findById(existingId)).thenReturn(Optional.of(existingTask));
        Task updatedTask = new Task(existingId, "col-1", updatedTaskDto.title(), updatedTaskDto.description(),  updatedTaskDto.deadline());
        when(taskRepository.save(updatedTask)).thenReturn(updatedTask);
        //WHEN
        Task actual = taskService.updateTask(existingId, updatedTaskDto);
        //THEN
        Task expected = new Task(existingId, "col-1", updatedTaskDto.title(), updatedTaskDto.description(),  updatedTaskDto.deadline());
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
        Task existingTask = new Task(existingId, "col-1", "Task 1", "Description 1", null);
        TaskDto updatedTaskDto = new TaskDto("col-2","Updated Task", "Updated Description",  null);
        Task updatedTask = new Task(existingId, "col-2", updatedTaskDto.title(), updatedTaskDto.description(), updatedTaskDto.deadline());
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
        Task expected = new Task(existingId, "col-2", updatedTaskDto.title(), updatedTaskDto.description(),  updatedTaskDto.deadline());
        verify(taskRepository).findById(existingId);
        verify(boardRepository).findByColumnId("col-1");
        verify(boardRepository).save(board);
        verify(taskRepository).save(updatedTask);
        assertEquals(expected, actual);
    }

    @Test
    void updateTask_whenTaskDoesNotExist_throwsElementNotFoundException() {
        //GIVEN
        String nonExistingId = "task-999";
        TaskDto updatedTaskDto = new TaskDto("col-1","Updated Task", "Updated Description",null);

        when(taskRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        //THEN
        ElementNotFoundException exception = assertThrows(ElementNotFoundException.class,
                //WHEN
                () -> taskService.updateTask(nonExistingId, updatedTaskDto));
        assertEquals("Task with ID task-999 not found", exception.getMessage());
    }

    @Test
    void deleteTask() {
        //GIVEN
        String existingId = "task-1";
        Task task = new Task(existingId, "col-1", "Task 1", "Description 1",  null);
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