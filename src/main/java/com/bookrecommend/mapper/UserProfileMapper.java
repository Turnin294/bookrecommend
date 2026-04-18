package com.bookrecommend.mapper;

import com.bookrecommend.config.JacksonTypeHandler;
import com.bookrecommend.entity.UserProfile;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserProfileMapper {

    @Select("SELECT * FROM user_profile WHERE user_id = #{userId}")
    @Results({
        @Result(property = "interests", column = "interests", typeHandler = JacksonTypeHandler.class),
        @Result(property = "favoriteCategories", column = "favorite_categories", typeHandler = JacksonTypeHandler.class)
    })
    UserProfile findByUserId(Long userId);

    @Insert("INSERT INTO user_profile(user_id, interests, favorite_categories) " +
            "VALUES(#{userId}, #{interests, typeHandler=com.bookrecommend.config.JacksonTypeHandler}, " +
            "#{favoriteCategories, typeHandler=com.bookrecommend.config.JacksonTypeHandler})")
    int insert(UserProfile profile);

    @Update("UPDATE user_profile SET interests = #{interests, typeHandler=com.bookrecommend.config.JacksonTypeHandler}, " +
            "favorite_categories = #{favoriteCategories, typeHandler=com.bookrecommend.config.JacksonTypeHandler} " +
            "WHERE user_id = #{userId}")
    int updateByUserId(UserProfile profile);
}
