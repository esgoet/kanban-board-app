package com.github.esgoet.backend.service;

import com.github.esgoet.backend.dto.BoardDto;
import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final IdService idService;

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
        return boardRepository.save(boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Board with ID " + id + " not found"))
                .withName(updatedBoard.name())
                .withColumns(updatedBoard.columns()));
    }

    public void deleteBoard(String id) {
        boardRepository.deleteById(id);
    }


}
