package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.BoardDto;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final IdService idService;
    private final TaskRepository taskRepository;

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board getBoardById(String id) {
        return boardRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Board with ID " + id + " not found"));
    }

    public Board createBoard(BoardDto board) {
        return boardRepository.save(new Board(idService.generateId(), board.name(), board.columns()));
    }

    public Board updateBoard(String id, BoardDto updatedBoard) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Board with ID " + id + " not found"));

        List<String> existingColumnIds = existingBoard.columns().stream().map(Column::id).toList();
        List<String> updatedColumnIds = updatedBoard.columns().stream().map(Column::id).toList();
        List<String> removedColumnIds = existingColumnIds.stream()
                .filter(columnId -> !updatedColumnIds.contains(columnId))
                .toList();

        removedColumnIds.forEach(taskRepository::deleteTasksByColumnId);

        return boardRepository.save(existingBoard
                .withName(updatedBoard.name())
                .withColumns(updatedBoard.columns()));
    }

    public void deleteBoard(String id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Board with ID " + id + " not found"));

        board.columns().forEach(column -> taskRepository.deleteTasksByColumnId(column.id()));

        boardRepository.deleteById(id);
    }


}
