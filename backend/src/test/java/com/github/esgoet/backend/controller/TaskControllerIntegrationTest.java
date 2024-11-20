package com.github.esgoet.backend.controller;

import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.model.Status;
import com.github.esgoet.backend.model.Task;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private BoardRepository boardRepository;

    private Task task;
    private Board board;

    @BeforeEach
    void setUp() {
        String deadline = "2025-01-01T00:00:00Z";
        task = new Task("task-1", "col-1", "Task 1", "This is task 1", Status.TODO, Instant.parse(deadline));
        board = new Board("1", "Board 1", List.of(new Column("col-1", "Column 1", List.of("task-1")), new Column("col-2", "Column 2", new ArrayList<>())));
    }

    @Test
    void getTasksByColumnId() throws Exception {
        //GIVEN
        taskRepository.save(task);
        //WHEN
        mockMvc.perform(get("/api/tasks/column/col-1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{
                          "id": "task-1",
                          "columnId": "col-1",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "TODO",
                          "deadline": "2025-01-01T00:00:00Z"
                        }]
                        """));
    }

    @Test
    void getTaskById() throws Exception {
        //GIVEN
        taskRepository.save(task);
        //WHEN
        mockMvc.perform(get("/api/tasks/task-1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "task-1",
                          "columnId": "col-1",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "TODO",
                          "deadline": "2025-01-01T00:00:00Z"
                        }"""));
    }

    @DirtiesContext
    @Test
    void createTask() throws Exception {
        //GIVEN
        boardRepository.save(board);
        //WHEN
        mockMvc.perform(post("/api/tasks/column/col-1")
                .contentType("application/json")
                .content("""
                        {
                          "columnId": "col-1",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "TODO",
                          "deadline": "2025-01-01T00:00:00Z"
                        }
                        """))
                //THEN
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                          "columnId": "col-1",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "TODO",
                          "deadline": "2025-01-01T00:00:00Z"
                        }
                        """))
                .andExpect(jsonPath("$.id").exists());
        //WHEN
        mockMvc.perform(get("/api/boards/1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "1",
                          "name": "Board 1",
                          "columns": [
                            {
                              "id": "col-1",
                              "name": "Column 1"
                            },
                            {
                              "id": "col-2",
                              "name": "Column 2",
                              "tasks": []
                            }
                          ]
                        }
                        """))
                .andExpect(jsonPath("$.columns[0].tasks[0]").exists());
    }

    @Test
    @DirtiesContext
    void updateTask() throws Exception {
        //GIVEN
        taskRepository.save(task);
        //WHEN
        mockMvc.perform(put("/api/tasks/task-1")
                .contentType("application/json")
                .content("""
                        {
                          "columnId": "col-1",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "DONE",
                          "deadline": "2025-01-01T00:00:00Z"
                        }
                        """))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "task-1",
                          "columnId": "col-1",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "DONE",
                          "deadline": "2025-01-01T00:00:00Z"
                        }
                        """));
    }

    @Test
    @DirtiesContext
    void updateTask_whenUpdatingColumns() throws Exception {
        //GIVEN
        taskRepository.save(task);
        boardRepository.save(board);
        //WHEN
        mockMvc.perform(put("/api/tasks/task-1")
                        .contentType("application/json")
                        .content("""
                        {
                          "columnId": "col-2",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "TODO",
                          "deadline": "2025-01-01T00:00:00Z"
                        }
                        """))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "task-1",
                          "columnId": "col-2",
                          "title": "Task 1",
                          "description": "This is task 1",
                          "status": "TODO",
                          "deadline": "2025-01-01T00:00:00Z"
                        }
                        """));
        //WHEN
        mockMvc.perform(get("/api/boards/1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "1",
                          "name": "Board 1",
                          "columns": [
                            {
                              "id": "col-1",
                              "name": "Column 1",
                              "tasks": []
                            },
                            {
                              "id": "col-2",
                              "name": "Column 2",
                              "tasks": ["task-1"]
                            }
                          ]
                        }
                        """));
    }

    @DirtiesContext
    @Test
    void deleteTask() throws Exception {
        //GIVEN
        taskRepository.save(task);
        boardRepository.save(board);
        //WHEN
        mockMvc.perform(delete("/api/tasks/task-1"))
                //THEN
                .andExpect(status().isNoContent());
        //WHEN
        mockMvc.perform(get("/api/tasks/task-1"))
                //THEN
                .andExpect(status().isNotFound());
        //WHEN
        mockMvc.perform(get("/api/boards/1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "1",
                          "name": "Board 1",
                          "columns": [
                            {
                              "id": "col-1",
                              "name": "Column 1",
                              "tasks": []
                            },
                            {
                              "id": "col-2",
                              "name": "Column 2",
                              "tasks": []
                            }
                          ]
                        }
                        """));
    }



}