package com.mykitchen.mapper;

import com.mykitchen.entity.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {
    @Select("SELECT * FROM comment WHERE recipe_id = #{recipeId} ORDER BY create_time DESC")
    List<Comment> findByRecipeId(Long recipeId);

    @Select("SELECT * FROM comment ORDER BY create_time DESC LIMIT #{limit}")
    List<Comment> findRecent(Long limit);

    @Insert("INSERT INTO comment(recipe_id, user_id, user_nickname, user_avatar, content, create_time) " +
            "VALUES(#{recipeId}, #{userId}, #{userNickname}, #{userAvatar}, #{content}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Delete("DELETE FROM comment WHERE id = #{id} AND user_id = #{userId}")
    int delete(@Param("id") Long id, @Param("userId") Long userId);
}
