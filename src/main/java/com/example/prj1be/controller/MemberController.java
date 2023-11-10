package com.example.prj1be.controller;

import com.example.prj1be.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/member")
public class MemberController {

    // 회원가입 기능 컨트롤러
    @PostMapping("signup")
    public void signup(@RequestBody Member member) {
        System.out.println("member = " + member);
    }
}
