package com.mykitchen.mapper;

import com.mykitchen.entity.Follow;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FollowMapper {
    @Select("SELECT * FROM follow WHERE follower_id = #{followerId}")
    List<Follow> findFollowing(Long followerId);

    @Select("SELECT * FROM follow WHERE following_id = #{followingId}")
    List<Follow> findFollowers(Long followingId);

    @Select("SELECT COUNT(*) FROM follow WHERE follower_id = #{followerId}")
    int countFollowing(Long followerId);

    @Select("SELECT COUNT(*) FROM follow WHERE following_id = #{followingId}")
    int countFollowers(Long followingId);

    @Select("SELECT * FROM follow WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    Follow find(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Insert("INSERT INTO follow(follower_id, following_id, create_time) VALUES(#{followerId}, #{followingId}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Follow follow);

    @Delete("DELETE FROM follow WHERE follower_id = #{followerId} AND following_id = #{followingId}")
    int delete(@Param("followerId") Long followerId, @Param("followingId") Long followingId);

    @Select("SELECT following_id FROM follow WHERE follower_id = #{followerId}")
    List<Long> findFollowingIds(Long followerId);
}
