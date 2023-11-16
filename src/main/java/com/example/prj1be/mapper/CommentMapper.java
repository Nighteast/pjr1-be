package com.example.prj1be.mapper;

import com.example.prj1be.domain.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    // 댓글 작성
    @Insert("""
            INSERT INTO comment (boardId, comment, memberId)
            VALUES (#{boardId}, #{comment}, #{memberId})
            """)
    int insert(Comment comment);

    // 댓글 리스트
    @Select("""
            SELECT c.id, c.boardId, m.nickName memberNickName, c.comment, c.inserted
            FROM comment c JOIN member m ON c.memberId = m.id
            WHERE c.boardId = #{boardId}
            ORDER BY c.id DESC
            """)
    List<Comment> selectByBoardId(Integer boardId);

    // 댓글 삭제
    @Delete("""
            DELETE FROM comment
            WHERE id = #{id}
            """)
    int deleteById(Integer id);

    // 댓글 삭제 권한 검증
    @Select("""
            SELECT * FROM comment
            WHERE id = #{id}
            """)
    Comment selectById(Integer id);

    @Update("""
            UPDATE comment
            SET comment = #{comment}
            WHERE id = #{id}
            """)
    int update(Comment comment);

    // 게시글 삭제 전 댓글들 삭제
    @Delete("""
            DELETE FROM comment
            WHERE boardId = #{boardId}
            """)
    int deleteByBoardId(Integer boardId);

    // 회원 탈퇴 전 댓글들 삭제
    @Delete("""
            DELETE FROM comment
            WHERE memberId = #{memberId}
            """)
    int deleteByMemberId(String memberId);
}
