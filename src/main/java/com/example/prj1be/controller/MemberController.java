package com.example.prj1be.controller;

import com.example.prj1be.domain.Member;
import com.example.prj1be.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService service;

    // 회원가입 기능 컨트롤러
    @PostMapping("signup")
    public ResponseEntity signup(@RequestBody Member member) {
        if (service.validate(member)) {
            if (service.add(member)) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // ID 중복 체크 기능 컨트롤러
    @GetMapping(value = "check", params = "id")
    public ResponseEntity checkId(String id) {
        if (service.getId(id) == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // email 중복 체크 기능 컨트롤러
    @GetMapping(value = "check", params = "email")
    public ResponseEntity checkEmail(String email) {
        if (service.getEmail(email) == null) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok().build();
        }
    }

    // 회원 목록 보기 컨트롤러
    @GetMapping("list")
    public List<Member> list() {
        return service.list();
    }

    // 회원 목록 상세 보기 컨트롤러
    @GetMapping
    public ResponseEntity<Member> view(String id) {
        // TODO : 로그인 했는 지? => 안 했으면 401
        // TODO : 자기 정보인지? => 아니면 403

        Member member = service.getMember(id);

        return ResponseEntity.ok(member);
    }

    // 회원 목록 상세보기 - 회원 탈퇴 기능
    @DeleteMapping
    public ResponseEntity delete(String id) {
        // TODO : 로그인 했는 지? => 안 했으면 401
        // TODO : 자기 정보인지? => 아니면 403

        if (service.deleteMember(id)) {
            return ResponseEntity.ok().build();
        }
            return ResponseEntity.internalServerError().build();
    }

    // 회원목록상세 - 회원수정
    @PutMapping("edit")
    public ResponseEntity edit(@RequestBody Member member) {
        // TODO : 로그인 했는지? 자기정보인지?

        if (service.update(member)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.internalServerError().build();
        }
    }
}
