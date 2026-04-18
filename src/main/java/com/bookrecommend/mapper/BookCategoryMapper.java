package com.bookrecommend.mapper;

import com.bookrecommend.entity.BookCategory;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookCategoryMapper {

    @Select("SELECT * FROM book_category ORDER BY sort DESC")
    List<BookCategory> findAll();

    @Insert("INSERT INTO book_category(name, parent_id, sort) VALUES(#{name}, #{parentId}, #{sort})")
    int insert(BookCategory category);

    @Delete("DELETE FROM book_category WHERE id = #{id}")
    int delete(Long id);
    
    @Select("SELECT * FROM book_category WHERE id = #{id}")
    BookCategory findById(Long id);
}
