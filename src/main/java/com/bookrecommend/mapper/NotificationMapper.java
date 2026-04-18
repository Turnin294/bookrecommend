package com.bookrecommend.mapper;

import com.bookrecommend.entity.Notification;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface NotificationMapper {

    @Select("SELECT * FROM notification WHERE user_id = #{userId} " +
            "AND (#{isRead} IS NULL OR is_read = #{isRead}) " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{size}")
    List<Notification> findList(@Param("userId") Long userId, 
                                @Param("isRead") Integer isRead,
                                @Param("offset") Integer offset, 
                                @Param("size") Integer size);

    @Select("SELECT COUNT(*) FROM notification WHERE user_id = #{userId} AND is_read = 0")
    int countUnread(Long userId);

    @Update("UPDATE notification SET is_read = 1 WHERE id = #{id} AND user_id = #{userId}")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId);

    @Update("UPDATE notification SET is_read = 1 WHERE user_id = #{userId}")
    void markAllAsRead(Long userId);

    @Insert("INSERT INTO notification(user_id, title, content, type, is_read) " +
            "VALUES(#{userId}, #{title}, #{content}, #{type}, 0)")
    int insert(Notification n);
}
