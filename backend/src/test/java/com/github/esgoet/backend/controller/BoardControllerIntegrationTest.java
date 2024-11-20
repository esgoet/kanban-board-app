package com.github.esgoet.backend.controller;

import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
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
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BoardControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private TaskRepository taskRepository;

    private Board board;
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task("task-1",  "col-1", "Task 1", "This is task 1", Instant.now());
        Column column = new Column("col-1", "Column 1", List.of("task-1"));
        board = new Board("1", "Board 1", List.of(column));
    }

    @Test
    void getAllBoards() throws Exception {
        //WHEN
        mockMvc.perform(get("/api/boards"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @DirtiesContext
    @Test
    void getBoardById() throws Exception {
        //GIVEN
        boardRepository.save(board);
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
                              "tasks": ["task-1"]
                            }
                          ]
                        }
                        """));
    }

    @DirtiesContext
    @Test
    void createBoard() throws Exception {
        //WHEN
        mockMvc.perform(post("/api/boards")
                .contentType("application/json")
                .content("""
                        {
                          "name": "Board 1",
                          "columns": []
                        }
                        """))
                //THEN
                .andExpect(status().isCreated())
                .andExpect(content().json("""
                        {
                          "name": "Board 1",
                          "columns": []
                        }
                        """))
                .andExpect(jsonPath("$.id").exists());

        //WHEN
        mockMvc.perform(get("/api/boards"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        [{
                          "name": "Board 1",
                          "columns": []
                        }]
                        """))
                .andExpect(jsonPath("$[0].id").exists());
    }

    @DirtiesContext
    @Test
    void updateBoard_whenAddingAColumnAndChangingName() throws Exception {
        //GIVEN
        boardRepository.save(board);
        //WHEN
        mockMvc.perform(put("/api/boards/1")
                .contentType("application/json")
                .content("""
                        {
                          "name": "Board 2",
                          "columns": [
                            {
                              "id": "col-1",
                              "name": "Column 1",
                              "tasks": ["task-1"]
                            },
                            {
                              "id": "col-2",
                              "name": "Column 2",
                              "tasks": []
                            }
                          ]
                        }
                        """))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "1",
                          "name": "Board 2",
                          "columns": [
                            {
                              "id": "col-1",
                              "name": "Column 1",
                              "tasks": ["task-1"]
                            },
                            {
                              "id": "col-2",
                              "name": "Column 2",
                              "tasks": []
                            }
                          ]
                        }
                        """));
        //WHEN
        mockMvc.perform(get("/api/boards/1"))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "1",
                          "name": "Board 2",
                          "columns": [
                            {
                              "id": "col-1",
                              "name": "Column 1",
                              "tasks": ["task-1"]
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

    @DirtiesContext
    @Test
    void updateBoard_whenDeletingAColumn() throws Exception {
        //GIVEN
        taskRepository.save(task);
        boardRepository.save(board);

        //WHEN
        mockMvc.perform(put("/api/boards/1")
                        .contentType("application/json")
                        .content("""
                        {
                          "name": "Board 2",
                          "columns": []
                        }
                        """))
                //THEN
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                          "id": "1",
                          "name": "Board 2",
                          "columns": []
                        }
                        """));
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
                          "name": "Board 2",
                          "columns": []
                        }
                        """));
    }

    @DirtiesContext
    @Test
    void deleteBoard() throws Exception {
        //GIVEN
        taskRepository.save(task);
        boardRepository.save(board);
        //WHEN
        mockMvc.perform(delete("/api/boards/1"))
                //THEN
                .andExpect(status().isNoContent());
        //WHEN
        mockMvc.perform(get("/api/tasks/task-1"))
                //THEN
                .andExpect(status().isNotFound());
        //WHEN
        mockMvc.perform(get("/api/boards/1"))
                //THEN
                .andExpect(status().isNotFound());

    }
}