package com.example.prj1be.mapper;

import com.example.prj1be.domain.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {


    @Insert("""
            INSERT INTO comment (boardId, comment, memberId)
            VALUES (#{boardId}, #{comment}, #{memberId})
            """)
    int insert(Comment comment);

    @Select("""
            SELECT *
            FROM comment
            WHERE boardId = #{boardId}
            """)
    List<Comment> selectByBoardId(Integer boardId);

    // 댓글 삭제
    @Delete("""
            DELETE FROM comment
            WHERE id = #{id}
            """)
    int deleteById(Integer id);

    // 댓글
    @Select("""
            SELECT * FROM comment
            WHERE id = #{id}
            """)
    Comment selectById(Integer id);

    // 댓글 수정
    @Update("""
            UPDATE comment
            SET comment = #{comment}
            WHERE id = #{id} 
            """)
    int editById(Comment comment);
}
