package com.example.prj1be.service;

import com.example.prj1be.domain.Board;
import com.example.prj1be.domain.Member;
import com.example.prj1be.mapper.BoardMapper;
import com.example.prj1be.mapper.CommentMapper;
import com.example.prj1be.mapper.FileMapper;
import com.example.prj1be.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardMapper mapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final FileMapper fileMapper;

    // 게시글 작성 후 저장 서비스
    public boolean save(Board board, MultipartFile[] files, Member login) {
        // 게시물 입력
        board.setWriter(login.getId());

        int cnt = mapper.insert(board);

        // boardFile 테이블에 files 정보 저장
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                // boardId, name
                fileMapper.insert(board.getId(), files[i].getOriginalFilename());

                // 실제 파일을 S3 bucket에 upload
                // 일단 local에 저장
                upload(board.getId(), files[i]);
            }
        }


        return cnt == 1;
    }

    private void upload(Integer boardId, MultipartFile file) {
        // 파일 저장 경로
        // C:\Temp\prj1\게시물번호\파일명
        try {
            File folder = new File("C:\\Temp\\prj1\\" + boardId);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String path = folder.getAbsolutePath() + "\\" + file.getOriginalFilename();
            File des = new File(path);
            file.transferTo(des);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
    // 게시글 페이지네이션
    // 게시글 검색
    public Map<String, Object> list(Integer page, String keyword) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();

        // 게시판 전체 글 갯수
        // int countAll = mapper.countAll();
        int countAll = mapper.countAll("%" + keyword + "%"); // %%
        int lastPageNumber = (countAll - 1) / 10 + 1;
        int startPageNumber = (page - 1) / 10 * 10 + 1;
        int endPageNumber = startPageNumber + 9;
        endPageNumber = Math.min(endPageNumber, lastPageNumber);
        int prevPageNumber = startPageNumber - 10;
        int nextPageNumber = endPageNumber + 1;
        int initialPageNumber = 1;

        pageInfo.put("lastPageNumber", lastPageNumber);
        pageInfo.put("currentPageNumber", page);
        pageInfo.put("startPageNumber", startPageNumber);
        pageInfo.put("endPageNumber", endPageNumber);
        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }
        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }
        if (page > 10) {
            pageInfo.put("initialPageNumber", initialPageNumber);
        }

        int from = (page - 1) * 10;
        map.put("boardList", mapper.selectAll(from, "%" + keyword + "%"));
        map.put("pageInfo", pageInfo);
        return map;
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
