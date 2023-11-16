package com.example.prj1be.service;

import com.example.prj1be.domain.Auth;
import com.example.prj1be.domain.Member;
import com.example.prj1be.mapper.BoardMapper;
import com.example.prj1be.mapper.CommentMapper;
import com.example.prj1be.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper mapper;
    private final BoardMapper boardMapper;
    private final CommentMapper commentMapper;

    public boolean add(Member member) {
        return mapper.insert(member) == 1;
    }

    // ID 중복 확인 서비스
    public String getId(String id) {
        return mapper.selectId(id);
    }

    // nickName 중복 확인 서비스
    public String getNickName(String nickName) {
        return mapper.selectNickName(nickName);
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


    // 회원 정보 보기 - 회원 탈퇴 서비스
    public boolean deleteMember(String id) {
        // TODO : 이 멤버가 작성한 댓글 삭제
        commentMapper.deleteByWriter(id);

        // 1. 이 멤버가 작성한 게시물 삭제
        boardMapper.deleteByWriter(id);

        // 2. 이 멤버 삭제

        return mapper.deleteById(id) == 1;
    }

    // 회원 정보 보기 - 회원 수정
    public boolean update(Member member) {
//        // password 변경 안 했을 시 적용
//        Member oldMember = mapper.selectById(member.getId());
//
//        if (member.getPassword().equals("")) {
//            member.setPassword(oldMember.getPassword());
//        }

        return mapper.update(member) == 1;
    }


    // 회원 로그인 서비스
    public boolean login(Member member, WebRequest request) {
        Member dbMember = mapper.selectById(member.getId());


        if (dbMember != null) {
            if (dbMember.getPassword().equals(member.getPassword())) {

                // 멤버아이디로 권한 정보들을 얻어 권한을 추가한다.
                List<Auth> auth = mapper.selectAuthById(member.getId());
                dbMember.setAuth(auth);

                dbMember.setPassword("");
                request.setAttribute("login", dbMember, RequestAttributes.SCOPE_SESSION);
                return true;
            }
        }

        return false;
    }

    // 회원 권한 검증 서비스
    public boolean hasAccess(String id, Member login) {
        if (isAdmin(login)) {
            return true;
        }

        return login.getId().equals(id);
    }

    public boolean isAdmin(Member login) {
        if (login.getAuth() != null) {
            return login.getAuth()
                    .stream()
                    .map(e -> e.getName())
                    .anyMatch(n -> n.equals("admin"));
        }
        return false;
    }
}
