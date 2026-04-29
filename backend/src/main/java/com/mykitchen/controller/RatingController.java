package com.mykitchen.controller;

import com.mykitchen.service.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "评分管理", description = "食谱评分功能")
public class RatingController {
    
    private final RatingService ratingService;
    
    @GetMapping("/recipe/{recipeId}")
    @Operation(summary = "获取食谱评分统计")
    public Result<Map<String, Object>> getRatingStats(@PathVariable Long recipeId) {
        return Result.success(ratingService.getRatingStats(recipeId));
    }
    
    @GetMapping("/recipe/{recipeId}/user/{userId}")
    @Operation(summary = "获取用户对食谱的评分")
    public Result<Integer> getUserRating(@PathVariable Long recipeId, @PathVariable Long userId) {
        Integer score = ratingService.getUserRating(recipeId, userId);
        return Result.success(score);
    }
    
    @PostMapping
    @Operation(summary = "评分/更新评分")
    public Result<Map<String, Object>> rateRecipe(@RequestBody Map<String, Long> params) {
        Long recipeId = params.get("recipeId");
        Long userId = params.get("userId");
        Integer score = params.get("score") != null ? params.get("score").intValue() : 0;
        
        if (recipeId == null || userId == null || score < 1 || score > 5) {
            return Result.error("参数错误");
        }
        
        try {
            Map<String, Object> result = ratingService.rateRecipe(recipeId, userId, score);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping
    @Operation(summary = "删除评分")
    public Result<Void> deleteRating(@RequestParam Long recipeId, @RequestParam Long userId) {
        ratingService.deleteRating(recipeId, userId);
        return Result.success(null);
    }
}
