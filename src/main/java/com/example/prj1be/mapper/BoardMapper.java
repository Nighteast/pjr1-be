package com.example.prj1be.mapper;

import com.example.prj1be.domain.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {
    //게시글 저장 맵퍼
    @Insert("""
            INSERT INTO board (title, content, writer)
            Values (#{title}, #{content}, #{writer})
            """)
    int insert(Board board);

    // 게시글 리스트 보기 맵퍼
    @Select("""
            SELECT b.id, 
                   b.title, 
                   b.writer,
                   m.nickName, 
                   b.inserted
            FROM board b JOIN member m ON b.writer = m.id
            ORDER BY b.id DESC
            """)
    List<Board> selectAll();

    // 한 게시글 보기 맵퍼
    @Select("""
            SELECT b.id, 
                   b.title, 
                   b.content, 
                   b.writer,
                   m.nickName, 
                   b.inserted
            FROM board b JOIN member m on b.writer = m.id
            WHERE b.id = #{id}
            """)
    Board selectById(Integer id);

    // 게시글 삭제 맵퍼
    @Delete("""
            DELETE FROM board
            WHERE id = #{id}
            """)
    int deleteById(Integer id);

    // 게시글 수정 맵퍼
    @Update("""
            UPDATE board
            SET title = #{title},
                content = #{content},
                writer=#{writer}
            WHERE id = #{id}
            """)
    int update(Board board);

    // 회원 탈퇴 시 게시글 삭제
    @Delete("""
            DELETE FROM board
            WHERE writer = #{writer}
            """)
    int deleteByWriter(String writer);
}
