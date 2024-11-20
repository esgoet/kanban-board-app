package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.BoardDto;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardServiceTest {
    private final IdService idService = mock(IdService.class);
    private final BoardRepository boardRepository = mock(BoardRepository.class);
    private final TaskRepository taskRepository = mock(TaskRepository.class);
    private final BoardService boardService = new BoardService(boardRepository, idService, taskRepository);

    @Test
    void getAllBoards_whenNoBoards_ReturnEmptyList() {
        //WHEN
        List<Board> actual = boardService.getAllBoards();
        //THEN
        List<Board> expected = List.of();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    void getAllBoards_whenOneBoard_ReturnOneBoard() {
        //GIVEN
        List<Board> boards = List.of(
                new Board("1", "Board 1", List.of()),
                new Board("2", "Board 2", List.of()));
        when(boardRepository.findAll()).thenReturn(boards);
        //WHEN
        List<Board> actual = boardService.getAllBoards();
        //THEN
        List<Board> expected = List.of(
                new Board("1", "Board 1", List.of()),
                new Board("2", "Board 2", List.of()));
        verify(boardRepository).findAll();
        assertEquals(expected, actual);
    }

    @Test
    void getBoardById_whenBoardExists_ReturnBoard() {
        //GIVEN
        Board board = new Board("1", "Board 1", List.of());
        when(boardRepository.findById("1")).thenReturn(Optional.of(board));
        //WHEN
        Board actual = boardService.getBoardById("1");
        //THEN
        Board expected = new Board("1", "Board 1", List.of());
        verify(boardRepository).findById("1");
        assertEquals(expected, actual);
    }

    @Test
    void getBoardById_whenNoBoard_ThrowsNoSuchElementException() {
        //GIVEN
        String nonExistentId = "999";
        when(boardRepository.findById(nonExistentId)).thenReturn(Optional.empty());
        //WHEN
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> boardService.getBoardById(nonExistentId));
        assertEquals("Board with ID 999 not found", exception.getMessage());
    }

    @Test
    void createBoard_savesBoardWithGeneratedId() {
        //GIVEN
        BoardDto boardDto = new BoardDto("New Board", List.of());
        String generatedId = "1";
        when(idService.generateId()).thenReturn(generatedId);

        Board savedBoard = new Board("1", "New Board", List.of());
        when(boardRepository.save(savedBoard)).thenReturn(savedBoard);
        //WHEN
        Board actual = boardService.createBoard(boardDto);
        //THEN
        Board expected = new Board("1", "New Board", List.of());
        verify(idService).generateId();
        verify(boardRepository).save(savedBoard);
        assertEquals(expected, actual);
    }

    @Test
    void updateBoard_whenBoardExists_returnUpdatedBoard() {
        //GIVEN
        String existingId = "1";
        Board existingBoard = new Board(existingId, "Existing Board", List.of());
        BoardDto updatedBoardDto = new BoardDto("Updated Board", List.of(new Column("2","Column 1", List.of())));

        when(boardRepository.findById(existingId)).thenReturn(Optional.of(existingBoard));
        Board updatedBoard = new Board(existingId, updatedBoardDto.name(), updatedBoardDto.columns());
        when(boardRepository.save(updatedBoard)).thenReturn(updatedBoard);
        //WHEN
        Board actual = boardService.updateBoard(existingId, updatedBoardDto);
        //THEN
        Board expected = new Board(existingId, updatedBoardDto.name(), updatedBoardDto.columns());
        verify(boardRepository).findById(existingId);
        verify(boardRepository).save(updatedBoard);
        assertEquals(expected, actual);

    }

    @Test
    void updateBoard_deletesTasksRelatedToRemovedColumns() {
        // GIVEN
        String boardId = "1";
        Column columnToStay = new Column("col-1", "Column 1", List.of());
        Column columnToBeDeleted = new Column("col-2", "Column 2", List.of("task-1", "task-2"));
        Board existingBoard = new Board(boardId, "Board 1", List.of(columnToStay, columnToBeDeleted));
        BoardDto updatedBoardDto = new BoardDto("Updated Board", List.of(columnToStay));

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));
        when(boardRepository.save(any(Board.class))).thenReturn(new Board(boardId, updatedBoardDto.name(), updatedBoardDto.columns()));
        doNothing().when(taskRepository).deleteTasksByColumnId("col-2");

        // WHEN
        Board actual = boardService.updateBoard(boardId, updatedBoardDto);

        // THEN
        Board expected = new Board(boardId, "Updated Board", List.of(columnToStay));
        verify(taskRepository).deleteTasksByColumnId("col-2");
        assertEquals(expected, actual);
    }

    @Test
    void updateBoard_whenBoardDoesNotExist_throwsNoSuchElementException() {
        //GIVEN
        String nonExistingId = "999";
        BoardDto updatedBoardDto = new BoardDto("Updated Board", List.of(new Column("2","Column 1", List.of())));

        when(boardRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        //THEN
        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                //WHEN
                () -> boardService.updateBoard(nonExistingId, updatedBoardDto));
        assertEquals("Board with ID 999 not found", exception.getMessage());
    }



    @Test
    void deleteBoard() {
        // GIVEN
        String boardId = "1";
        Column column1 = new Column("col-1", "Column 1", List.of("task-1", "task-2"));
        Column column2 = new Column("col-2", "Column 2", List.of("task-3"));
        Board board = new Board(boardId, "Board 1", List.of(column1, column2));

        when(boardRepository.findById(boardId)).thenReturn(Optional.of(board));
        doNothing().when(taskRepository).deleteTasksByColumnId(anyString());
        doNothing().when(boardRepository).deleteById(boardId);

        // WHEN
        boardService.deleteBoard(boardId);

        // THEN
        verify(taskRepository).deleteTasksByColumnId("col-1");
        verify(taskRepository).deleteTasksByColumnId("col-2");
        verify(boardRepository).deleteById(boardId);
    }
}