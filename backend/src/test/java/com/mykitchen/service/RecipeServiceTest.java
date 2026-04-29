package com.mykitchen.service;

import com.mykitchen.entity.Recipe;
import com.mykitchen.entity.Ingredient;
import com.mykitchen.entity.Step;
import com.mykitchen.mapper.RecipeMapper;
import com.mykitchen.mapper.IngredientMapper;
import com.mykitchen.mapper.StepMapper;
import com.mykitchen.controller.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeMapper recipeMapper;

    @Mock
    private IngredientMapper ingredientMapper;

    @Mock
    private StepMapper stepMapper;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RecipeService recipeService;

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
    void getAllRecipes_ShouldReturnCachedRecipes_WhenCacheExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("recipe:list:all")).thenReturn(testRecipes);

        List<Recipe> result = recipeService.getAllRecipes();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("红烧肉", result.get(0).getTitle());
        verify(recipeMapper, never()).findAll();
    }

    @Test
    void getAllRecipes_ShouldQueryDatabase_WhenCacheNotExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("recipe:list:all")).thenReturn(null);
        when(recipeMapper.findAll()).thenReturn(testRecipes);

        List<Recipe> result = recipeService.getAllRecipes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeMapper).findAll();
        verify(valueOperations).set(eq("recipe:list:all"), eq(testRecipes), anyLong(), any());
    }

    @Test
    void getRecipeById_ShouldReturnCachedRecipe_WhenCacheExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("recipe:detail:1")).thenReturn(testRecipe);

        Recipe result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        assertEquals("红烧肉", result.getTitle());
        verify(recipeMapper, never()).findById(any());
    }

    @Test
    void getRecipeById_ShouldQueryDatabase_WhenCacheNotExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("recipe:detail:1")).thenReturn(null);
        when(recipeMapper.findById(1L)).thenReturn(testRecipe);

        Recipe result = recipeService.getRecipeById(1L);

        assertNotNull(result);
        assertEquals("红烧肉", result.getTitle());
        verify(recipeMapper).findById(1L);
        verify(recipeMapper).incrementViews(1L);
    }

    @Test
    void getRecipesByCategory_ShouldReturnCachedList_WhenCacheExists() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("recipe:category:家常菜")).thenReturn(testRecipes);

        List<Recipe> result = recipeService.getRecipesByCategory("家常菜");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeMapper, never()).findByCategory(any());
    }

    @Test
    void searchRecipes_ShouldReturnResults() {
        when(recipeMapper.searchByKeyword("红烧")).thenReturn(testRecipes);

        List<Recipe> result = recipeService.searchRecipes("红烧");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(recipeMapper).searchByKeyword("红烧");
    }

    @Test
    void getPopularRecipes_ShouldReturnLimitedResults() {
        when(recipeMapper.findPopular(10L)).thenReturn(testRecipes);

        List<Recipe> result = recipeService.getPopularRecipes(10);

        assertNotNull(result);
        verify(recipeMapper).findPopular(10L);
    }

    @Test
    void getIngredients_ShouldReturnIngredients() {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setRecipeId(1L);
        ingredient.setName("五花肉");
        ingredient.setAmount("500g");

        when(ingredientMapper.findByRecipeId(1L)).thenReturn(Arrays.asList(ingredient));

        List<Ingredient> result = recipeService.getIngredients(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("五花肉", result.get(0).getName());
    }

    @Test
    void getSteps_ShouldReturnSteps() {
        Step step = new Step();
        step.setId(1L);
        step.setRecipeId(1L);
        step.setStepNumber(1);
        step.setContent("切块");

        when(stepMapper.findByRecipeId(1L)).thenReturn(Arrays.asList(step));

        List<Step> result = recipeService.getSteps(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("切块", result.get(0).getContent());
    }

    @Test
    void createRecipe_ShouldInsertRecipeAndRelatedData() {
        when(recipeMapper.insert(any(Recipe.class))).thenReturn(1);
        doNothing().when(ingredientMapper).batchInsert(anyList());
        doNothing().when(stepMapper).batchInsert(anyList());
        when(redisTemplate.delete(anyString())).thenReturn(true);

        Recipe newRecipe = new Recipe();
        newRecipe.setTitle("新食谱");
        newRecipe.setCategory("家常菜");

        Ingredient ingredient = new Ingredient();
        ingredient.setName("食材");
        ingredient.setAmount("100g");

        Step step = new Step();
        step.setStepNumber(1);
        step.setContent("步骤1");

        Recipe result = recipeService.createRecipe(newRecipe, Arrays.asList(ingredient), Arrays.asList(step));

        assertNotNull(result);
        verify(recipeMapper).insert(any(Recipe.class));
        verify(ingredientMapper).batchInsert(anyList());
        verify(stepMapper).batchInsert(anyList());
    }

    @Test
    void deleteRecipe_ShouldDeleteRecipeAndRelatedData() {
        when(redisTemplate.delete(anyString())).thenReturn(true);
        doNothing().when(ingredientMapper).deleteByRecipeId(1L);
        doNothing().when(stepMapper).deleteByRecipeId(1L);
        when(recipeMapper.delete(1L)).thenReturn(1);

        recipeService.deleteRecipe(1L);

        verify(ingredientMapper).deleteByRecipeId(1L);
        verify(stepMapper).deleteByRecipeId(1L);
        verify(recipeMapper).delete(1L);
    }

    @Test
    void getRecipesByPage_ShouldReturnPagedResults() {
        when(recipeMapper.findByPage(0, 10)).thenReturn(testRecipes);
        when(recipeMapper.countAll()).thenReturn(25L);

        PageResult<Recipe> result = recipeService.getRecipesByPage(1, 10);

        assertNotNull(result);
        assertEquals(1, result.getList().size());
        assertEquals(25, result.getTotal());
        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());
        assertEquals(3, result.getTotalPages());
    }

    @Test
    void countFavorites_ShouldReturnCount() {
        when(recipeMapper.findById(1L)).thenReturn(testRecipe);

        int count = recipeService.countFavorites(1L);

        assertEquals(10, count);
    }

    @Test
    void invalidateRecipeCache_ShouldDeleteCacheKeys() {
        when(redisTemplate.delete(anyString())).thenReturn(true);

        recipeService.invalidateRecipeCache(1L);

        verify(redisTemplate).delete("recipe:list:all");
        verify(redisTemplate).delete("recipe:detail:1");
    }
}
