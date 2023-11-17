package com.example.prj1be.mapper;

import com.example.prj1be.domain.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface LikeMapper {

    // 좋아요 취소
    @Delete("""
            DELETE FROM boardLike
            WHERE boardId = #{boardId}
            AND memberId = #{memberId}
            """)
    int delete(Like like);

    // 좋아요 삽입
    @Insert("""
            INSERT INTO boardlike (memberId, boardId) 
            VALUES (#{memberId},#{boardId})
            """)
    int insert(Like like);

    // 게시물의 좋아요 갯수 새기
    @Select("""
            SELECT COUNT(id) FROM boardlike
            WHERE boardId = #{boardId}
            """)
    int countByBoardId(Integer boardId);

    @Select("""
        SELECT * 
        FROM boardLike
        WHERE 
                boardId = #{boardId}
            AND memberId = #{memberId}   
        """)
    Like selectByBoardIdAndMemberId(Integer boardId, String memberId);
}
