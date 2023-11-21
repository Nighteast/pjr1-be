package com.example.prj1be.service;

import com.example.prj1be.domain.Board;
import com.example.prj1be.domain.BoardFile;
import com.example.prj1be.domain.Member;
import com.example.prj1be.mapper.BoardMapper;
import com.example.prj1be.mapper.CommentMapper;
import com.example.prj1be.mapper.FileMapper;
import com.example.prj1be.mapper.LikeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class BoardService {

    private final BoardMapper mapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;
    private final FileMapper fileMapper;

    private final S3Client s3;

    @Value("${image.file.prefix}")
    private String urlPrefix;
    @Value("${aws.s3.bucket.name}")
    private String bucket;

    // 게시글 작성 후 저장 서비스

    public boolean save(Board board, MultipartFile[] files, Member login) throws IOException {
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

    private void upload(Integer boardId, MultipartFile file) throws IOException {
        String key = "prj1/" + boardId + "/" + file.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
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
    public Map<String, Object> list(Integer page, String keyword, String category) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> pageInfo = new HashMap<>();

        // 게시판 전체 글 갯수
        // int countAll = mapper.countAll();
        int countAll = mapper.countAll("%" + keyword + "%", category); // %%
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
        map.put("boardList", mapper.selectAll(from, "%" + keyword + "%", category));
        map.put("pageInfo", pageInfo);
        return map;
    }

    // 한 게시글 보기 서비스
    public Board get(Integer id) {
        Board board = mapper.selectById(id);

        List<BoardFile> boardFiles = fileMapper.selectNamesByBoardId(id);

        for (BoardFile boardFile : boardFiles) {
            String url = urlPrefix + "prj1/" + id + "/" + boardFile.getName();
            boardFile.setUrl(url);
        }

        board.setFiles(boardFiles);

        return board;
    }

    // 게시글 삭제 서비스
    public boolean remove(Integer id) {
        //1. 게시물에 달린 댓글들 지우기
        commentMapper.deleteByBoardId(id);

        // 좋아요 레코드 지우기
        likeMapper.deleteByBoardId(id);

        // 첨부파일 이미지 지우기 (S3, 레코드)
        deleteFile(id);


        return mapper.deleteById(id) == 1;
    }

    private void deleteFile(Integer id) {
        // 파일명 조회
        List<BoardFile> boardFiles = fileMapper.selectNamesByBoardId(id);

        // s3 bucket objects 지우기
        for (BoardFile file : boardFiles) {
            String key = "prj1/" + id + "/" + file.getName();

            DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            s3.deleteObject(objectRequest);
        }

        // 첨부파일 이미지 레코드 지우기
        fileMapper.deleteByBoardId(id);
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
    public boolean update(Board board, List<Integer> removeFileIds, MultipartFile[] uploadFiles) throws IOException {

        // 파일 지우기
        if (removeFileIds != null) {
            for (Integer id : removeFileIds) {
                 // s3에서 지우기
                BoardFile file = fileMapper.selectById(id);
                String key = "prj1/" + board.getId() + "/" + file.getName();
                DeleteObjectRequest objectRequest = DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build();
                s3.deleteObject(objectRequest);

                // db에서 지우기
                fileMapper.deleteById(id);
            }
        }

        // 파일 추가하기
        if (uploadFiles != null) {
            // s3에 올리기
            for (MultipartFile file : uploadFiles) {
                upload(board.getId(), file);
                // db에 추가하기
                fileMapper.insert(board.getId(), file.getOriginalFilename());
            }
        }

        return mapper.update(board) == 1;
    }
}
