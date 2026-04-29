package com.mykitchen.mapper;

import com.mykitchen.entity.Ingredient;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface IngredientMapper {
    @Select("SELECT * FROM ingredient WHERE recipe_id = #{recipeId} ORDER BY sort_order")
    List<Ingredient> findByRecipeId(Long recipeId);

    @Insert("<script>" +
            "INSERT INTO ingredient(recipe_id, name, amount, sort_order) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.recipeId}, #{item.name}, #{item.amount}, #{item.sortOrder})" +
            "</foreach>" +
            "</script>")
    int batchInsert(List<Ingredient> ingredients);

    @Delete("DELETE FROM ingredient WHERE recipe_id = #{recipeId}")
    int deleteByRecipeId(Long recipeId);
}
