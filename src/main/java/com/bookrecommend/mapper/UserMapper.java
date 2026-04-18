package com.bookrecommend.mapper;

import com.bookrecommend.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO user(username, password, email, role, status) VALUES(#{username}, #{password}, #{email}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Update("UPDATE user SET email = #{email}, avatar = #{avatar} WHERE id = #{id}")
    int updateInfo(User user);

    @Update("UPDATE user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Select("SELECT * FROM user WHERE (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%')) LIMIT #{offset}, #{size}")
    List<User> findList(@Param("keyword") String keyword, @Param("offset") Integer offset, @Param("size") Integer size);

    @Select("SELECT COUNT(*) FROM user WHERE (username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%'))")
    long count(@Param("keyword") String keyword);
}
