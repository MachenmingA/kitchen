package com.mykitchen.service;

import com.mykitchen.entity.Rating;
import com.mykitchen.mapper.RatingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RatingService {
    
    private final RatingMapper ratingMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String RATING_CACHE_PREFIX = "rating:";
    private static final long CACHE_EXPIRE_HOURS = 1;
    
    public Map<String, Object> rateRecipe(Long recipeId, Long userId, Integer score) {
        if (score < 1 || score > 5) {
            throw new RuntimeException("评分必须在1-5之间");
        }
        
        Rating existing = ratingMapper.findByRecipeAndUser(recipeId, userId);
        
        if (existing != null) {
            existing.setScore(score);
            ratingMapper.update(existing);
        } else {
            Rating rating = new Rating();
            rating.setRecipeId(recipeId);
            rating.setUserId(userId);
            rating.setScore(score);
            ratingMapper.insert(rating);
        }
        
        invalidateCache(recipeId);
        
        return getRatingStats(recipeId);
    }
    
    public void deleteRating(Long recipeId, Long userId) {
        ratingMapper.delete(recipeId, userId);
        invalidateCache(recipeId);
    }
    
    public Map<String, Object> getRatingStats(Long recipeId) {
        String cacheKey = RATING_CACHE_PREFIX + recipeId;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> cached = (Map<String, Object>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        Double avgScore = ratingMapper.getAverageScore(recipeId);
        int count = ratingMapper.getCount(recipeId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("averageScore", avgScore != null ? Math.round(avgScore * 10) / 10.0 : 0.0);
        stats.put("totalCount", count);
        
        redisTemplate.opsForValue().set(cacheKey, stats, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        
        return stats;
    }
    
    public Integer getUserRating(Long recipeId, Long userId) {
        Rating rating = ratingMapper.findByRecipeAndUser(recipeId, userId);
        return rating != null ? rating.getScore() : null;
    }
    
    private void invalidateCache(Long recipeId) {
        redisTemplate.delete(RATING_CACHE_PREFIX + recipeId);
    }
}
