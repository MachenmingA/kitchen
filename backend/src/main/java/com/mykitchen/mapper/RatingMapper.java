package com.mykitchen.mapper;

import com.mykitchen.entity.Rating;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RatingMapper {
    @Select("SELECT * FROM rating WHERE recipe_id = #{recipeId}")
    List<Rating> findByRecipeId(Long recipeId);

    @Select("SELECT * FROM rating WHERE recipe_id = #{recipeId} AND user_id = #{userId}")
    Rating findByRecipeAndUser(@Param("recipeId") Long recipeId, @Param("userId") Long userId);

    @Select("SELECT AVG(score) FROM rating WHERE recipe_id = #{recipeId}")
    Double getAverageScore(Long recipeId);

    @Select("SELECT COUNT(*) FROM rating WHERE recipe_id = #{recipeId}")
    int getCount(Long recipeId);

    @Insert("INSERT INTO rating(recipe_id, user_id, score, create_time) " +
            "VALUES(#{recipeId}, #{userId}, #{score}, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Rating rating);

    @Update("UPDATE rating SET score = #{score} WHERE id = #{id}")
    int update(Rating rating);

    @Delete("DELETE FROM rating WHERE recipe_id = #{recipeId} AND user_id = #{userId}")
    int delete(@Param("recipeId") Long recipeId, @Param("userId") Long userId);
}
