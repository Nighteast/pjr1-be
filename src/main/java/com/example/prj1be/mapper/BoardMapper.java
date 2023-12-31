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
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Board board);

    // 게시글 리스트 보기 맵퍼
    @Select("""
            <script>
            SELECT b.id,
                b.title,
                b.content,
                b.writer,
                m.nickName,
                b.inserted,
                COUNT(DISTINCT c.id) countComment,
                COUNT(DISTINCT l.id) countLike,
                COUNT(DISTINCT f.id) countFile
            FROM board b JOIN member m on b.writer = m.id
                        LEFT JOIN comment c on b.id = c.boardId
                        LEFT JOIN boardLike l on b.id = l.boardId
                        LEFT JOIN boardFile f on b.id = f.boardId
            WHERE 
                <trim prefixOverrides="OR">
                    <if test="category == 'all' or category == 'title'">
                        OR title LIKE #{keyword}
                    </if>
                    <if test="category == 'all' or category == 'content'">
                        OR content LIKE #{keyword}
                    </if>
                </trim>
            
            GROUP BY b.id
            ORDER BY b.id DESC
            LIMIT #{from}, 10
            </script>
            """)
    List<Board> selectAll(Integer from, String keyword, String category);

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
                content = #{content}
            WHERE id = #{id}
            """)
    int update(Board board);

    // 회원 탈퇴 시 게시글 삭제
    @Delete("""
            DELETE FROM board
            WHERE writer = #{writer}
            """)
    int deleteByWriter(String writer);

    // 멤버가 작성한 게시물들 조회
    @Select("""
            SELECT id FROM board
            WHERE writer = #{writer}
            """)
    List<Integer> selectIdListByMemberId(String writer);

    @Select("""
            <script>
            SELECT COUNT(*) FROM board
            WHERE 
                <trim prefixOverrides="OR">
                    <if test="category == 'all' or category == 'title'">
                        OR title LIKE #{keyword}
                    </if>
                    <if test="category == 'all' or category == 'content'">
                        OR content LIKE #{keyword}
                    </if>
                </trim>
            </script>
            """)
    int countAll(String keyword, String category);
}
