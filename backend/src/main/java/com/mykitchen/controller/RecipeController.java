package com.mykitchen.controller;

import com.mykitchen.controller.PageResult;
import com.mykitchen.entity.Recipe;
import com.mykitchen.entity.Ingredient;
import com.mykitchen.entity.Step;
import com.mykitchen.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@CrossOrigin(origins = "*")
@Tag(name = "食谱管理", description = "食谱的增删改查")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @GetMapping
    @Operation(summary = "获取所有食谱（分页）")
    public Result<PageResult<Recipe>> getAllRecipes(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(recipeService.getRecipesByPage(page, pageSize));
    }

    @GetMapping("/all")
    @Operation(summary = "获取所有食谱（不分页）")
    public Result<List<Recipe>> getAllRecipesSimple() {
        return Result.success(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取食谱详情")
    public Result<RecipeDetail> getRecipeDetail(@PathVariable Long id) {
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            return Result.error("食谱不存在");
        }
        RecipeDetail detail = new RecipeDetail();
        detail.setRecipe(recipe);
        detail.setIngredients(recipeService.getIngredients(id));
        detail.setSteps(recipeService.getSteps(id));
        return Result.success(detail);
    }

    @GetMapping("/category/{category}")
    public Result<PageResult<Recipe>> getByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(recipeService.getRecipesByCategoryPage(category, page, pageSize));
    }

    @GetMapping("/search")
    public Result<List<Recipe>> search(@RequestParam String keyword) {
        return Result.success(recipeService.searchRecipes(keyword));
    }

    @GetMapping("/popular")
    public Result<List<Recipe>> getPopular(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(recipeService.getPopularRecipes(limit));
    }

    @PostMapping
    public Result<Recipe> createRecipe(@RequestBody RecipeRequest request) {
        Recipe recipe = recipeService.createRecipe(request.getRecipe(), 
                request.getIngredients(), request.getSteps());
        return Result.success(recipe);
    }

    @PutMapping("/{id}")
    public Result<Recipe> updateRecipe(@PathVariable Long id, @RequestBody RecipeRequest request) {
        Recipe recipe = request.getRecipe();
        recipe.setId(id);
        Recipe updated = recipeService.updateRecipe(recipe,
                request.getIngredients(), request.getSteps());
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return Result.success(null);
    }

    @GetMapping("/user/{userId}")
    public Result<List<Recipe>> getUserRecipes(@PathVariable Long userId) {
        return Result.success(recipeService.getRecipesByUser(userId));
    }

    public static class RecipeDetail {
        private Recipe recipe;
        private List<Ingredient> ingredients;
        private List<Step> steps;

        public Recipe getRecipe() { return recipe; }
        public void setRecipe(Recipe recipe) { this.recipe = recipe; }
        public List<Ingredient> getIngredients() { return ingredients; }
        public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
        public List<Step> getSteps() { return steps; }
        public void setSteps(List<Step> steps) { this.steps = steps; }
    }

    public static class RecipeRequest {
        private Recipe recipe;
        private List<Ingredient> ingredients;
        private List<Step> steps;

        public Recipe getRecipe() { return recipe; }
        public void setRecipe(Recipe recipe) { this.recipe = recipe; }
        public List<Ingredient> getIngredients() { return ingredients; }
        public void setIngredients(List<Ingredient> ingredients) { this.ingredients = ingredients; }
        public List<Step> getSteps() { return steps; }
        public void setSteps(List<Step> steps) { this.steps = steps; }
    }
}
