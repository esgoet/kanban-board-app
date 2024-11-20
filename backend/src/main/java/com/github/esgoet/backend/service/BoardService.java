package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.BoardDto;
import com.github.esgoet.backend.exception.ElementNotFoundException;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.model.Column;
import com.github.esgoet.backend.repository.BoardRepository;
import com.github.esgoet.backend.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final IdService idService;
    private final TaskRepository taskRepository;

    private static final String ELEMENT_TYPE = "Board";

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board getBoardById(String id) {
        return boardRepository.findById(id).orElseThrow(() -> new ElementNotFoundException(ELEMENT_TYPE, id));
    }

    public Board createBoard(BoardDto board) {
        return boardRepository.save(new Board(idService.generateId(), board.name(), board.columns()));
    }

    public Board updateBoard(String id, BoardDto updatedBoard) {
        Board existingBoard = boardRepository.findById(id)
                .orElseThrow(() -> new ElementNotFoundException(ELEMENT_TYPE, id));

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
                .orElseThrow(() -> new ElementNotFoundException(ELEMENT_TYPE, id));

        board.columns().forEach(column -> taskRepository.deleteTasksByColumnId(column.id()));

        boardRepository.deleteById(id);
    }


}
