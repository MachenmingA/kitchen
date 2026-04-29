package com.mykitchen.mapper;

import com.mykitchen.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE id = #{id}")
    User findById(Long id);

    @Select("SELECT * FROM user WHERE username = #{username}")
    User findByUsername(String username);

    @Insert("INSERT INTO user(username, password, nickname, avatar, bio, create_time, update_time) " +
            "VALUES(#{username}, #{password}, #{nickname}, #{avatar}, #{bio}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE user SET nickname=#{nickname}, avatar=#{avatar}, bio=#{bio}, update_time=NOW() WHERE id=#{id}")
    int update(User user);
}
