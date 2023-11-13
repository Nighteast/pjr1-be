package com.example.prj1be.mapper;

import com.example.prj1be.domain.Member;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MemberMapper {

    // 회원가입 기능 매퍼
    @Insert("""
            INSERT INTO member (id, password, nickName, email)
            VALUES (#{id}, #{password},#{nickName}, #{email})
            """)
    int insert(Member member);

    // ID 중복 확인 매퍼
    @Select("""
            SELECT id FROM member
            WHERE id = #{id}
            """)
    String selectId(String id);

    // nickName 중복 확인 매퍼
    @Select("""
            SELECT nickName FROM member
            WHERE nickName = #{nickName}
            """)
    String selectNickName(String nickName);

    // 이메일 중복 확인 매퍼
    @Select("""
            SELECT email FROM member
            WHERE email = #{email}
            """)
    String selectEmail(String email);

    // 회원 목록 보기 매퍼
    @Select("""
            SELECT id, password, email, inserted
            FROM member
            ORDER BY inserted DESC;
            """)
    List<Member> selectAll();

    @Select("""
            SELECT *
            FROM member
            WHERE id = #{id}
            """)
    Member selectById(String id);

    // 회원 탈퇴
    @Delete("""
            DELETE FROM member
            WHERE id = #{id}
            """)
    int deleteById(String id);

    // 회원 수정 with.DynamicSQL
    @Update("""
            <script>
            UPDATE member
            SET
                <if test="password != ''">
                password = #{password},
                </if>
                email = #{email}
            WHERE id = #{id}
            </script>
            """)
    int update(Member member);

}
