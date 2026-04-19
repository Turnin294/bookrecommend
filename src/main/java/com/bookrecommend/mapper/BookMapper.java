package com.bookrecommend.mapper;

import com.bookrecommend.entity.Book;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BookMapper {

    // 分页过滤查询，这部分在 XML 里写
    List<Book> findList(@Param("keyword") String keyword, 
                       @Param("categoryId") Long categoryId, 
                       @Param("tags") List<String> tags, 
                       @Param("status") Integer status,
                       @Param("offset") Integer offset, 
                       @Param("size") Integer size);

    long count(@Param("keyword") String keyword, 
               @Param("categoryId") Long categoryId, 
               @Param("tags") List<String> tags,
               @Param("status") Integer status);

    @Select("SELECT * FROM book WHERE id = #{id}")
    @ResultMap("BookResultMap")
    Book findById(Long id);

    @Insert("INSERT INTO book(title, author, isbn, category_id, description, cover, publisher, publish_date, tags, content, stock, status) " +
            "VALUES(#{title}, #{author}, #{isbn}, #{categoryId}, #{description}, #{cover}, #{publisher}, #{publishDate}, " +
            "#{tags, typeHandler=com.bookrecommend.config.JacksonTypeHandler}, #{content}, #{stock}, #{status})")
    int insert(Book book);

    @Update("UPDATE book SET title=#{title}, author=#{author}, isbn=#{isbn}, category_id=#{categoryId}, " +
            "description=#{description}, cover=#{cover}, publisher=#{publisher}, publish_date=#{publishDate}, " +
            "tags=#{tags, typeHandler=com.bookrecommend.config.JacksonTypeHandler}, content=#{content}, stock=#{stock}, status=#{status} " +
            "WHERE id=#{id}")
    int update(Book book);

    @Update("UPDATE book SET status=#{status} WHERE id=#{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT b.* FROM book b " +
            "JOIN user_behavior ub ON b.id = ub.book_id " +
            "WHERE ub.user_id IN (SELECT user_id FROM user_behavior WHERE book_id = #{bookId} AND behavior_type IN (2,3,4)) " +
            "AND b.id != #{bookId} " +
            "GROUP BY b.id ORDER BY COUNT(ub.id) DESC LIMIT #{limit}")
    @ResultMap("BookResultMap")
    List<Book> findRelatedBooks(@Param("bookId") Long bookId, @Param("limit") Integer limit);
}
