package com.example.prj1be.mapper;

import com.example.prj1be.domain.Board;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BoardMapper {
    @Insert("""
            INSERT INTO board (title, content, writer)
            Values (#{title}, #{content}, #{writer})
            """)
    int insert(Board board);

    @Select("""
            SELECT 
                title, 
                content, 
                writer, 
                inserted
            FROM board
            """)
    List<String> getList();

}
