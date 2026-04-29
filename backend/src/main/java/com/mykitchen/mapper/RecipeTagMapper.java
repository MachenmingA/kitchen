package com.mykitchen.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecipeTagMapper {
    @Insert("INSERT INTO recipe_tag(recipe_id, tag_id) VALUES(#{recipeId}, #{tagId})")
    int insert(@Param("recipeId") Long recipeId, @Param("tagId") Long tagId);

    @Delete("DELETE FROM recipe_tag WHERE recipe_id = #{recipeId}")
    int deleteByRecipeId(Long recipeId);

    @Select("SELECT tag_id FROM recipe_tag WHERE recipe_id = #{recipeId}")
    List<Long> findTagIdsByRecipeId(Long recipeId);

    @Delete("DELETE FROM recipe_tag WHERE tag_id = #{tagId}")
    int deleteByTagId(Long tagId);
}
