package com.mykitchen.mapper;

import com.mykitchen.entity.Tag;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TagMapper {
    @Select("SELECT * FROM tag ORDER BY recipe_count DESC")
    List<Tag> findAll();

    @Select("SELECT * FROM tag ORDER BY recipe_count DESC LIMIT #{limit}")
    List<Tag> findPopular(Long limit);

    @Select("SELECT * FROM tag WHERE id = #{id}")
    Tag findById(Long id);

    @Select("SELECT * FROM tag WHERE name = #{name}")
    Tag findByName(String name);

    @Insert("INSERT INTO tag(name, recipe_count, create_time) VALUES(#{name}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Tag tag);

    @Update("UPDATE tag SET recipe_count = #{recipeCount} WHERE id = #{id}")
    int updateRecipeCount(@Param("id") Long id, @Param("recipeCount") Integer recipeCount);

    @Delete("DELETE FROM tag WHERE id = #{id}")
    int delete(Long id);
}
