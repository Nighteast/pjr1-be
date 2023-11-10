package com.example.prj1be.service;

import com.example.prj1be.domain.Board;
import com.example.prj1be.mapper.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper mapper;

    // 게시글 작성 후 저장 서비스
    public boolean save(Board board) {
        return mapper.insert(board) == 1;
    }

    // 게시글 저장 시 null 혹은 black 확인 서비스
    public boolean validate(Board board) {
        // 보드 객체가 null이면 false 반환
        if (board == null) {
            return false;
        }
        // 본문, 타이틀, 작성자가 null 혹은 빈칸이면 false반환
        if (board.getContent() == null || board.getContent().isBlank()) {
            return false;
        }
        if (board.getTitle() == null || board.getTitle().isBlank()) {
            return false;
        }
        if (board.getWriter() == null || board.getWriter().isBlank()) {
            return false;
        }
        // 그 이외 true 반환
        return true;
    }

    // 게시글 리스트 보기 서비스
    public List<Board> list() {
        return mapper.selectAll();
    }

    // 한 게시글 보기 서비스
    public Board get(Integer id) {
        return mapper.selectById(id);
    }

    // 게시글 삭제 서비스
    public boolean remove(Integer id) {
        return mapper.deleteById(id) == 1;
    }

    // 게시글 수정 서비스
    public boolean update(Board board) {
        return mapper.update(board) == 1;
    }
}
