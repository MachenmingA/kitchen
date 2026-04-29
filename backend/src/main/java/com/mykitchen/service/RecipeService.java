package com.mykitchen.service;

import com.mykitchen.entity.Recipe;
import com.mykitchen.entity.Ingredient;
import com.mykitchen.entity.Step;
import com.mykitchen.mapper.RecipeMapper;
import com.mykitchen.mapper.IngredientMapper;
import com.mykitchen.mapper.StepMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RecipeService {
    
    private final RecipeMapper recipeMapper;
    private final IngredientMapper ingredientMapper;
    private final StepMapper stepMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String RECIPE_LIST_CACHE = "recipe:list:all";
    private static final String RECIPE_CACHE_PREFIX = "recipe:detail:";
    private static final String CATEGORY_CACHE_PREFIX = "recipe:category:";
    private static final long CACHE_EXPIRE_MINUTES = 30;
    
    @SuppressWarnings("unchecked")
    public List<Recipe> getAllRecipes() {
        List<Recipe> cached = (List<Recipe>) redisTemplate.opsForValue().get(RECIPE_LIST_CACHE);
        if (cached != null) {
            return cached;
        }
        
        List<Recipe> recipes = recipeMapper.findAll();
        redisTemplate.opsForValue().set(RECIPE_LIST_CACHE, recipes, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return recipes;
    }
    
    public Recipe getRecipeById(Long id) {
        String cacheKey = RECIPE_CACHE_PREFIX + id;
        
        Recipe cached = (Recipe) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            recipeMapper.incrementViews(id);
            return cached;
        }
        
        Recipe recipe = recipeMapper.findById(id);
        if (recipe != null) {
            redisTemplate.opsForValue().set(cacheKey, recipe, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
            recipeMapper.incrementViews(id);
        }
        return recipe;
    }
    
    public List<Recipe> getRecipesByCategory(String category) {
        String cacheKey = CATEGORY_CACHE_PREFIX + category;
        
        List<Recipe> cached = (List<Recipe>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        List<Recipe> recipes = recipeMapper.findByCategory(category);
        redisTemplate.opsForValue().set(cacheKey, recipes, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        return recipes;
    }
    
    public List<Recipe> searchRecipes(String keyword) {
        return recipeMapper.searchByKeyword(keyword);
    }
    
    public List<Recipe> getPopularRecipes(int limit) {
        return recipeMapper.findPopular((long) limit);
    }
    
    public List<Ingredient> getIngredients(Long recipeId) {
        return ingredientMapper.findByRecipeId(recipeId);
    }
    
    public List<Step> getSteps(Long recipeId) {
        return stepMapper.findByRecipeId(recipeId);
    }
    
    @Transactional
    public Recipe createRecipe(Recipe recipe, List<Ingredient> ingredients, List<Step> steps) {
        recipeMapper.insert(recipe);
        if (ingredients != null && !ingredients.isEmpty()) {
            ingredients.forEach(i -> i.setRecipeId(recipe.getId()));
            ingredientMapper.batchInsert(ingredients);
        }
        if (steps != null && !steps.isEmpty()) {
            steps.forEach(s -> s.setRecipeId(recipe.getId()));
            stepMapper.batchInsert(steps);
        }
        
        invalidateRecipeCache(recipe.getId());
        
        return recipe;
    }
    
    @Transactional
    public Recipe updateRecipe(Recipe recipe, List<Ingredient> ingredients, List<Step> steps) {
        recipeMapper.update(recipe);
        
        if (ingredients != null) {
            ingredientMapper.deleteByRecipeId(recipe.getId());
            ingredients.forEach(i -> i.setRecipeId(recipe.getId()));
            ingredientMapper.batchInsert(ingredients);
        }
        
        if (steps != null) {
            stepMapper.deleteByRecipeId(recipe.getId());
            steps.forEach(s -> s.setRecipeId(recipe.getId()));
            stepMapper.batchInsert(steps);
        }
        
        invalidateRecipeCache(recipe.getId());
        
        return recipe;
    }
    
    public List<Recipe> getRecipesByUser(Long userId) {
        return recipeMapper.findByUserId(userId);
    }
    
    @Transactional
    public void deleteRecipe(Long id) {
        ingredientMapper.deleteByRecipeId(id);
        stepMapper.deleteByRecipeId(id);
        recipeMapper.delete(id);
        invalidateRecipeCache(id);
    }
    
    public void updateFavoritesCount(Long recipeId) {
        int count = countFavorites(recipeId);
        recipeMapper.updateFavoritesCount(recipeId, count);
        invalidateRecipeCache(recipeId);
    }
    
    public int countFavorites(Long recipeId) {
        Recipe recipe = recipeMapper.findById(recipeId);
        return recipe != null ? recipe.getFavoritesCount() : 0;
    }
    
    public void invalidateRecipeCache(Long recipeId) {
        redisTemplate.delete(RECIPE_LIST_CACHE);
        redisTemplate.delete(RECIPE_CACHE_PREFIX + recipeId);
    }
    
    public void invalidateCategoryCache(String category) {
        redisTemplate.delete(CATEGORY_CACHE_PREFIX + category);
        redisTemplate.delete(RECIPE_LIST_CACHE);
    }
}
