package com.example.prj1be.controller;

import com.example.prj1be.domain.Board;
import com.example.prj1be.domain.Member;
import com.example.prj1be.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService service;

    // 게시글 작성 기능
    @PostMapping("add")
    public ResponseEntity add(Board board,
                              @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] files,
                              @SessionAttribute(value = "login", required = false) Member login) throws IOException {

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                System.out.println("file = " + files[i].getOriginalFilename());
                System.out.println("file.getSize() = " + files[i].getSize());
            }
        }

        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 인증이 false면 badRequest 응답 보내기
        if (!service.validate(board)) {
            return ResponseEntity.badRequest().build();
        }
        // 저장이 true면 ok응답, false면 internalServerError응답
        if (service.save(board, files, login)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 게시글 리스트 보기 기능
    // 게시글 리스트 검색 기능
    // /api/board/list?p=2
    // /api/board/list?k=java
    @GetMapping("list")
    public Map<String, Object> list(
            @RequestParam(value = "p", defaultValue = "1") Integer page,
            @RequestParam(value = "k", defaultValue = "") String keyword,
            @RequestParam(value = "c", defaultValue = "all") String category) {

        return service.list(page, keyword, category);
    }

    // 한 게시글 보기 기능, id에 맞는 board값 가져오기
    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id) {
        return service.get(id);
    }

    // 게시글 삭제 기능
    @DeleteMapping("remove/{id}")
    public ResponseEntity remove(@PathVariable Integer id,
                                 @SessionAttribute(value = "login", required = false) Member login) {
        // 401 권한없음, 로그인 하지 않았음.
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 403 접근권한 없음, 본인 아이디가 아님
        if (!service.hasAccess(id, login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // remove가 true면 ok응답, false면 internalServerError응답
        if (service.remove(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 게시글 수정 기능
    @PutMapping("edit")
    public ResponseEntity edit(Board board,
                               @RequestParam(value = "uploadFiles[]", required = false) MultipartFile[] uploadFiles,
                               @RequestParam(value = "removeFileIds[]",required = false) List<Integer> removeFileIds,
                               @SessionAttribute(value = "login", required = false) Member login) throws IOException {
        // 401
        if (login == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 403
        if (!service.hasAccess(board.getId(), login)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (service.validate(board)) {
            if (service.update(board, removeFileIds, uploadFiles)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

}
