package com.bookrecommend.mapper;

import com.bookrecommend.entity.UserBehavior;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserBehaviorMapper {

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND book_id = #{bookId} AND behavior_type = #{type} LIMIT 1")
    UserBehavior findOneByBehavior(@Param("userId") Long userId, @Param("bookId") Long bookId, @Param("type") Integer type);

    @Insert("INSERT INTO user_behavior(user_id, book_id, behavior_type, score, duration, comment) " +
            "VALUES(#{userId}, #{bookId}, #{behaviorType}, #{score}, #{duration}, #{comment})")
    int insert(UserBehavior behavior);

    @Select("SELECT ub.*, ub.book_id as bookId, b.title as bookTitle, b.cover as bookCover, b.author as bookAuthor " +
            "FROM user_behavior ub JOIN book b ON ub.book_id = b.id " +
            "WHERE ub.user_id = #{userId} AND ub.behavior_type = #{type} " +
            "ORDER BY ub.created_at DESC")
    List<Map<String, Object>> findMyBehaviorsWithType(@Param("userId") Long userId, @Param("type") Integer type);

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<UserBehavior> findByUserId(Long userId);

    @Select("SELECT ub.*, u.username as username, b.title as bookTitle " +
            "FROM user_behavior ub " +
            "JOIN user u ON ub.user_id = u.id " +
            "JOIN book b ON ub.book_id = b.id " +
            "WHERE ub.behavior_type = 3 " +
            "ORDER BY ub.created_at DESC LIMIT #{offset}, #{size}")
    List<Map<String, Object>> findAllRatingsWithUserInfo(@Param("offset") Integer offset, @Param("size") Integer size);

    @Select("SELECT COUNT(*) FROM user_behavior WHERE behavior_type = 3")
    long countAllRatings();

    @Delete("DELETE FROM user_behavior WHERE id = #{id}")
    int deleteById(Long id);

    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND book_id = #{bookId} AND behavior_type = 5 ORDER BY created_at DESC LIMIT 1")
    UserBehavior findLatestProgress(@Param("userId") Long userId, @Param("bookId") Long bookId);

    @Select("SELECT COUNT(*) FROM user_behavior WHERE user_id = #{userId}")
    long countByUserId(Long userId);

    @Select("SELECT ub.id, ub.score, ub.comment, ub.created_at as createdAt, u.username, u.avatar " +
            "FROM user_behavior ub JOIN user u ON ub.user_id = u.id " +
            "WHERE ub.book_id = #{bookId} AND ub.behavior_type = 3 AND ub.comment IS NOT NULL AND ub.comment != '' " +
            "ORDER BY ub.created_at DESC LIMIT #{limit}")
    java.util.List<java.util.Map<String, Object>> findPublicReviewsByBookId(@Param("bookId") Long bookId, @Param("limit") Integer limit);
}
