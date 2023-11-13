package com.example.prj1be.service;

import com.example.prj1be.domain.Member;
import com.example.prj1be.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper mapper;

    public boolean add(Member member) {
        return mapper.insert(member) == 1;
    }

    // ID 중복 확인 서비스
    public String getId(String id) {
        return mapper.selectId(id);
    }

    // 이메일 중복 확인 서비스
    public String getEmail(String email) {
        return mapper.selectEmail(email);
    }

    // 회원가입 검증 서비스
    public boolean validate(Member member) {
        if (member == null) {
            return false;
        }
        if (member.getEmail().isBlank()) {
            return false;
        }
        if (member.getPassword().isBlank()) {
            return false;
        }
        if (member.getId().isBlank()) {
            return false;
        }
        return true;
    }

    // 회원 목록 보기 서비스
    public List<Member> list() {
        return mapper.selectAll();
    }

    // 회원 정보 보기 서비스
    public Member getMember(String id) {
        return mapper.selectById(id);
    }


}
