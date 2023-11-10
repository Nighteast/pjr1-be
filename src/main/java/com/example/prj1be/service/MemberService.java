package com.example.prj1be.service;

import com.example.prj1be.domain.Member;
import com.example.prj1be.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper mapper;

    public void add(Member member) {
        mapper.insert(member);
    }

    // ID 중복 확인 서비스
    public String getId(String id) {
        return mapper.selectId(id);
    }

    // 이메일 중복 확인 서비스
    public String getEmail(String email) {
        return mapper.selectEmail(email);
    }
}
