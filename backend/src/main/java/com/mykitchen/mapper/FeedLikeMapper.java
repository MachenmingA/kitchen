package com.mykitchen.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface FeedLikeMapper {
    @Insert("INSERT INTO feed_like(feed_id, user_id, create_time) VALUES(#{feedId}, #{userId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(@Param("feedId") Long feedId, @Param("userId") Long userId);

    @Delete("DELETE FROM feed_like WHERE feed_id = #{feedId} AND user_id = #{userId}")
    int delete(@Param("feedId") Long feedId, @Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM feed_like WHERE feed_id = #{feedId}")
    int countByFeedId(Long feedId);

    @Select("SELECT * FROM feed_like WHERE feed_id = #{feedId} AND user_id = #{userId}")
    Object find(@Param("feedId") Long feedId, @Param("userId") Long userId);
}
