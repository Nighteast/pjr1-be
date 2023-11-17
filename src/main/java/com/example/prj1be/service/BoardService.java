package com.example.prj1be.service;

import com.example.prj1be.domain.Board;
import com.example.prj1be.domain.Member;
import com.example.prj1be.mapper.BoardMapper;
import com.example.prj1be.mapper.CommentMapper;
import com.example.prj1be.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper mapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;

    // 게시글 작성 후 저장 서비스
    public boolean save(Board board, Member login) {
        board.setWriter(login.getId());

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

        // 그 이외 true 반환
        return true;
    }

    // 게시글 리스트 보기 서비스
    public List<Board> list(Integer page) {

        int from = (page -1) * 10;

        return mapper.selectAll(from);
    }

    // 한 게시글 보기 서비스
    public Board get(Integer id) {
        return mapper.selectById(id);
    }

    // 게시글 삭제 서비스
    public boolean remove(Integer id) {
        //1. 게시물에 달린 댓글들 지우기
        commentMapper.deleteByBoardId(id);

        // 좋아요 레코드 지우기
        likeMapper.deleteByBoardId(id);

        return mapper.deleteById(id) == 1;
    }

    // 삭제 시 ID 권한 확인
    public boolean hasAccess(Integer id, Member login) {
        if (login == null) {
            return false;
        }

        if (login.isAdmin()) {
            return true;
        }

        Board board = mapper.selectById(id);

        return board.getWriter().equals(login.getId());
    }



    // 게시글 수정 서비스
    public boolean update(Board board) {
        return mapper.update(board) == 1;
    }

}
