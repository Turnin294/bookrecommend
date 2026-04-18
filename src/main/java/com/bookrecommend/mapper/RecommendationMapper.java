package com.bookrecommend.mapper;

import com.bookrecommend.entity.Recommendation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecommendationMapper {

    @Select("SELECT * FROM recommendation WHERE user_id = #{userId} ORDER BY score DESC LIMIT #{size}")
    List<Recommendation> findByUserId(@Param("userId") Long userId, @Param("size") Integer size);

    @Insert("INSERT INTO recommendation(user_id, book_id, score, reason, algorithm) " +
            "VALUES(#{userId}, #{bookId}, #{score}, #{reason}, #{algorithm})")
    int insert(Recommendation rec);

    @Select("DELETE FROM recommendation WHERE user_id = #{userId}")
    void deleteByUserId(Long userId);
}
