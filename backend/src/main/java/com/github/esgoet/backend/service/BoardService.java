package com.github.esgoet.backend.service;

import com.github.esgoet.backend.model.Board;
import com.github.esgoet.backend.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BoardService {
    private BoardRepository boardRepository;

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    public Board createBoard(Board board) {
        return boardRepository.save(board);
    }

    public Board updateBoard(String id, Board updatedBoard) {
        return boardRepository.save(boardRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Board with ID " + id + " not found"))
                .withName(updatedBoard.name())
                .withColumns(updatedBoard.columns()));
    }

    public void deleteBoard(String id) {
        boardRepository.deleteById(id);
    }
}
