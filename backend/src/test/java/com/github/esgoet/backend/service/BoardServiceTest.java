package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.BoardDto;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.repository.BoardRepository;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BoardServiceTest {
    private IdService idService = mock(IdService.class);
    private BoardRepository boardRepository = mock(BoardRepository.class);
    private BoardService boardService = new BoardService(boardRepository, idService);

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
    void updateBoard_whenBoardDoesNotExist_throwsException() {
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
        //GIVEN
        String existingId = "1";
        doNothing().when(boardRepository).deleteById(existingId);
        //WHEN
        boardService.deleteBoard(existingId);
        //THEN
        verify(boardRepository).deleteById(existingId);
    }
}