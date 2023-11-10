package com.example.prj1be.controller;

import com.example.prj1be.domain.Board;
import com.example.prj1be.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService service;

    // 게시글 작성 기능
    @PostMapping("add")
    public ResponseEntity add(@RequestBody Board board) {
        // 인증이 false면 badRequest 응답 보내기
        if (!service.validate(board)) {
            return ResponseEntity.badRequest().build();
        }
        // 저장이 true면 ok응답, false면 internalServerError응답
        if (service.save(board)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 게시글 리스트 보기 기능
    @GetMapping("list")
    public List<Board> list() {
        return service.list();
    }

    // 한 게시글 보기 기능, id에 맞는 board값 가져오기
    @GetMapping("id/{id}")
    public Board get(@PathVariable Integer id) {
        return service.get(id);
    }

    // 게시글 삭제 기능
    @DeleteMapping("remove/{id}")
    public ResponseEntity remove(@PathVariable Integer id) {
        // remove가 true면 ok응답, false면 internalServerError응답
        if (service.remove(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 게시글 수정 기능
    @PutMapping("edit")
    public void edit(@RequestBody Board board) {
//        System.out.println("board = " + board);
        service.update(board);
    }
}
