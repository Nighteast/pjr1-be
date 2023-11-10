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
            SELECT id, title, writer, inserted
            FROM board
            ORDER BY id DESC
            """)
    List<Board> selectAll();

    // 한 게시글 보기 맵퍼
    @Select("""
            SELECT id, title, content, writer, inserted
            FROM board
            WHERE id = #{id}
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
}