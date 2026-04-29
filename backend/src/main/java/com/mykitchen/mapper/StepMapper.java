package com.mykitchen.mapper;

import com.mykitchen.entity.Step;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StepMapper {
    @Select("SELECT * FROM step WHERE recipe_id = #{recipeId} ORDER BY step_number")
    List<Step> findByRecipeId(Long recipeId);

    @Insert("<script>" +
            "INSERT INTO step(recipe_id, step_number, content, image_url) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.recipeId}, #{item.stepNumber}, #{item.content}, #{item.imageUrl})" +
            "</foreach>" +
            "</script>")
    int batchInsert(List<Step> steps);

    @Delete("DELETE FROM step WHERE recipe_id = #{recipeId}")
    int deleteByRecipeId(Long recipeId);
}
