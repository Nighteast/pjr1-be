package com.example.prj1be.mapper;

import com.example.prj1be.domain.Member;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MemberMapper {

    // 회원가입 기능 매퍼
    @Insert("""
            INSERT INTO member (id, password, email)
            VALUES (#{id}, #{password}, #{email})
            """)
    int insert(Member member);

    // ID 중복 확인 매퍼
    @Select("""
            SELECT id FROM member
            WHERE id = #{id}
            """)
    String selectId(String id);
}
