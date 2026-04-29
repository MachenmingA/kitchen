package com.mykitchen.service;

import com.mykitchen.entity.Favorite;
import com.mykitchen.entity.Recipe;
import com.mykitchen.mapper.FavoriteMapper;
import com.mykitchen.mapper.RecipeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;
    @Autowired
    private RecipeMapper recipeMapper;

    public boolean isFavorited(Long userId, Long recipeId) {
        return favoriteMapper.findByUserAndRecipe(userId, recipeId) != null;
    }

    public List<Recipe> getUserFavorites(Long userId) {
        List<Favorite> favorites = favoriteMapper.findByUserId(userId);
        return favorites.stream()
                .map(f -> recipeMapper.findById(f.getRecipeId()))
                .collect(Collectors.toList());
    }

    public void addFavorite(Long userId, Long recipeId) {
        if (favoriteMapper.findByUserAndRecipe(userId, recipeId) == null) {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setRecipeId(recipeId);
            favoriteMapper.insert(favorite);
            Recipe recipe = recipeMapper.findById(recipeId);
            recipeMapper.updateFavoritesCount(recipeId, recipe.getFavoritesCount() + 1);
        }
    }

    public void removeFavorite(Long userId, Long recipeId) {
        Favorite favorite = favoriteMapper.findByUserAndRecipe(userId, recipeId);
        if (favorite != null) {
            favoriteMapper.delete(userId, recipeId);
            Recipe recipe = recipeMapper.findById(recipeId);
            recipeMapper.updateFavoritesCount(recipeId, Math.max(0, recipe.getFavoritesCount() - 1));
        }
    }
}
