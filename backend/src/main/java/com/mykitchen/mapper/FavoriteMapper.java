package com.mykitchen.mapper;

import com.mykitchen.entity.Favorite;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FavoriteMapper {
    @Select("SELECT * FROM favorite WHERE user_id = #{userId}")
    List<Favorite> findByUserId(Long userId);

    @Select("SELECT * FROM favorite WHERE user_id = #{userId} AND recipe_id = #{recipeId}")
    Favorite findByUserAndRecipe(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

    @Insert("INSERT INTO favorite(user_id, recipe_id, create_time) VALUES(#{userId}, #{recipeId}, NOW())")
    int insert(Favorite favorite);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND recipe_id = #{recipeId}")
    int delete(@Param("userId") Long userId, @Param("recipeId") Long recipeId);

    @Select("SELECT COUNT(*) FROM favorite WHERE recipe_id = #{recipeId}")
    int countByRecipeId(Long recipeId);
}
