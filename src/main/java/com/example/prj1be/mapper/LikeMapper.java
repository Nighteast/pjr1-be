package com.example.prj1be.mapper;

import com.example.prj1be.domain.Like;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

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
}
