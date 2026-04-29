package com.mykitchen.mapper;

import com.mykitchen.entity.Recipe;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecipeMapper {
    @Select("SELECT * FROM recipe ORDER BY create_time DESC")
    List<Recipe> findAll();

    @Select("SELECT * FROM recipe WHERE id = #{id}")
    Recipe findById(Long id);

    @Select("SELECT * FROM recipe WHERE category = #{category} ORDER BY create_time DESC")
    List<Recipe> findByCategory(String category);

    @Select("SELECT * FROM recipe WHERE title LIKE CONCAT('%', #{keyword}, '%') ORDER BY create_time DESC")
    List<Recipe> searchByKeyword(String keyword);

    @Select("SELECT * FROM recipe ORDER BY favorites_count DESC LIMIT #{limit}")
    List<Recipe> findPopular(Long limit);

    @Select("SELECT * FROM recipe WHERE author_id = #{userId} ORDER BY create_time DESC")
    List<Recipe> findByUserId(Long userId);

    @Select("SELECT * FROM recipe ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Recipe> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM recipe")
    long countAll();

    @Select("SELECT * FROM recipe WHERE category = #{category} ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Recipe> findByCategoryPage(@Param("category") String category, @Param("offset") int offset, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM recipe WHERE category = #{category}")
    long countByCategory(String category);

    @Insert("INSERT INTO recipe(title, description, image_url, category, difficulty, cook_time, servings, " +
            "author_id, author_name, author_avatar, favorites_count, views_count, create_time, update_time) " +
            "VALUES(#{title}, #{description}, #{imageUrl}, #{category}, #{difficulty}, #{cookTime}, #{servings}, " +
            "#{authorId}, #{authorName}, #{authorAvatar}, 0, 0, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Recipe recipe);

    @Update("UPDATE recipe SET title=#{title}, description=#{description}, image_url=#{imageUrl}, " +
            "category=#{category}, difficulty=#{difficulty}, cook_time=#{cookTime}, servings=#{servings}, " +
            "update_time=NOW() WHERE id=#{id}")
    int update(Recipe recipe);

    @Update("UPDATE recipe SET favorites_count = #{favoritesCount} WHERE id = #{id}")
    int updateFavoritesCount(@Param("id") Long id, @Param("favoritesCount") Integer favoritesCount);

    @Update("UPDATE recipe SET views_count = views_count + 1 WHERE id = #{id}")
    int incrementViews(Long id);

    @Delete("DELETE FROM recipe WHERE id = #{id}")
    int delete(Long id);
}
