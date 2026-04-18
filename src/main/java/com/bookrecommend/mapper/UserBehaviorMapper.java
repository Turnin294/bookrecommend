package com.bookrecommend.mapper;

import com.bookrecommend.entity.UserBehavior;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserBehaviorMapper {

    @Insert("INSERT INTO user_behavior(user_id, book_id, behavior_type, score, duration) " +
            "VALUES(#{userId}, #{bookId}, #{behaviorType}, #{score}, #{duration})")
    int insert(UserBehavior behavior);

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserBehavior> findByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM user_behavior WHERE user_id = #{userId}")
    long countByUserId(Long userId);
}
