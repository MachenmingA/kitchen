package com.mykitchen.controller;

import com.mykitchen.entity.Favorite;
import com.mykitchen.entity.Recipe;
import com.mykitchen.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "*")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/user/{userId}")
    public Result<List<Recipe>> getUserFavorites(@PathVariable Long userId) {
        return Result.success(favoriteService.getUserFavorites(userId));
    }

    @GetMapping("/check")
    public Result<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long recipeId) {
        return Result.success(favoriteService.isFavorited(userId, recipeId));
    }

    @PostMapping
    public Result<Void> addFavorite(@RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(request.getUserId(), request.getRecipeId());
        return Result.success(null);
    }

    @DeleteMapping
    public Result<Void> removeFavorite(@RequestParam Long userId, @RequestParam Long recipeId) {
        favoriteService.removeFavorite(userId, recipeId);
        return Result.success(null);
    }

    public static class FavoriteRequest {
        private Long userId;
        private Long recipeId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Long getRecipeId() { return recipeId; }
        public void setRecipeId(Long recipeId) { this.recipeId = recipeId; }
    }
}
