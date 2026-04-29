package com.mykitchen.mapper;

import com.mykitchen.entity.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FeedMapper {
    @Select("SELECT * FROM feed ORDER BY create_time DESC LIMIT #{limit}")
    List<Feed> findRecent(Long limit);

    @Select("SELECT * FROM feed WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Feed> findByUserId(Long userId);

    @Select("SELECT * FROM feed WHERE id = #{id}")
    Feed findById(Long id);

    @Insert("INSERT INTO feed(user_id, content, image_url, create_time) " +
            "VALUES(#{userId}, #{content}, #{imageUrl}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Feed feed);

    @Delete("DELETE FROM feed WHERE id = #{id}")
    int delete(Long id);
}
