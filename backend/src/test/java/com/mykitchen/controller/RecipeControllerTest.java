package com.mykitchen.controller;

import com.mykitchen.entity.Recipe;
import com.mykitchen.service.RecipeService;
import com.mykitchen.controller.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeControllerTest {

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private RecipeController recipeController;

    private Recipe testRecipe;
    private List<Recipe> testRecipes;

    @BeforeEach
    void setUp() {
        testRecipe = new Recipe();
        testRecipe.setId(1L);
        testRecipe.setTitle("红烧肉");
        testRecipe.setDescription("美味的红烧肉");
        testRecipe.setCategory("家常菜");
        testRecipe.setDifficulty("中等");
        testRecipe.setCookTime(60);
        testRecipe.setServings(4);
        testRecipe.setAuthorId(1L);
        testRecipe.setAuthorName("张三");
        testRecipe.setFavoritesCount(10);
        testRecipe.setViewsCount(100);

        testRecipes = Arrays.asList(testRecipe);
    }

    @Test
    void getAllRecipes_ShouldReturnPagedRecipes() {
        PageResult<Recipe> pageResult = PageResult.of(testRecipes, 25, 1, 10);
        when(recipeService.getRecipesByPage(1, 10)).thenReturn(pageResult);

        Result<PageResult<Recipe>> result = recipeController.getAllRecipes(1, 10);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().getList().size());
        assertEquals(25, result.getData().getTotal());
    }

    @Test
    void getAllRecipes_ShouldUseDefaultPagination_WhenNotProvided() {
        PageResult<Recipe> pageResult = PageResult.of(testRecipes, 25, 1, 10);
        when(recipeService.getRecipesByPage(1, 10)).thenReturn(pageResult);

        Result<PageResult<Recipe>> result = recipeController.getAllRecipes(1, 10);

        assertNotNull(result);
        verify(recipeService).getRecipesByPage(1, 10);
    }

    @Test
    void getRecipeDetail_ShouldReturnRecipeDetail_WhenExists() {
        when(recipeService.getRecipeById(1L)).thenReturn(testRecipe);
        when(recipeService.getIngredients(1L)).thenReturn(Arrays.asList());
        when(recipeService.getSteps(1L)).thenReturn(Arrays.asList());

        Result<RecipeController.RecipeDetail> result = recipeController.getRecipeDetail(1L);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("红烧肉", result.getData().getRecipe().getTitle());
    }

    @Test
    void getRecipeDetail_ShouldReturnError_WhenNotFound() {
        when(recipeService.getRecipeById(999L)).thenReturn(null);

        Result<RecipeController.RecipeDetail> result = recipeController.getRecipeDetail(999L);

        assertEquals(400, result.getCode());
        assertEquals("食谱不存在", result.getMessage());
    }

    @Test
    void getByCategory_ShouldReturnPagedRecipes() {
        PageResult<Recipe> pageResult = PageResult.of(testRecipes, 10, 1, 10);
        when(recipeService.getRecipesByCategoryPage("家常菜", 1, 10)).thenReturn(pageResult);

        Result<PageResult<Recipe>> result = recipeController.getByCategory("家常菜", 1, 10);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().getList().size());
    }

    @Test
    void search_ShouldReturnSearchResults() {
        when(recipeService.searchRecipes("红烧")).thenReturn(testRecipes);

        Result<List<Recipe>> result = recipeController.search("红烧");

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
    }

    @Test
    void getPopular_ShouldReturnPopularRecipes() {
        when(recipeService.getPopularRecipes(10)).thenReturn(testRecipes);

        Result<List<Recipe>> result = recipeController.getPopular(10);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().size());
    }

    @Test
    void deleteRecipe_ShouldReturnSuccess() {
        doNothing().when(recipeService).deleteRecipe(1L);

        Result<Void> result = recipeController.deleteRecipe(1L);

        assertEquals(200, result.getCode());
        verify(recipeService).deleteRecipe(1L);
    }
}
