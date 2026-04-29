package com.mykitchen.service;

import com.mykitchen.entity.Tag;
import com.mykitchen.mapper.RecipeTagMapper;
import com.mykitchen.mapper.TagMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TagService {
    
    private final TagMapper tagMapper;
    private final RecipeTagMapper recipeTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TAG_LIST_CACHE = "tag:list:all";
    private static final String TAG_POPULAR_CACHE = "tag:list:popular";
    private static final long CACHE_EXPIRE_HOURS = 6;
    
    public List<Tag> getAllTags() {
        @SuppressWarnings("unchecked")
        List<Tag> cached = (List<Tag>) redisTemplate.opsForValue().get(TAG_LIST_CACHE);
        if (cached != null) {
            return cached;
        }
        
        List<Tag> tags = tagMapper.findAll();
        redisTemplate.opsForValue().set(TAG_LIST_CACHE, tags, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return tags;
    }
    
    public List<Tag> getPopularTags(int limit) {
        String cacheKey = TAG_POPULAR_CACHE + ":" + limit;
        
        @SuppressWarnings("unchecked")
        List<Tag> cached = (List<Tag>) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        List<Tag> tags = tagMapper.findPopular((long) limit);
        redisTemplate.opsForValue().set(cacheKey, tags, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        return tags;
    }
    
    public List<Tag> getTagsByRecipeId(Long recipeId) {
        List<Long> tagIds = recipeTagMapper.findTagIdsByRecipeId(recipeId);
        return tagIds.stream()
                .map(tagMapper::findById)
                .filter(tag -> tag != null)
                .toList();
    }
    
    @Transactional
    public void addTagsToRecipe(Long recipeId, List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        
        for (Long tagId : tagIds) {
            recipeTagMapper.insert(recipeId, tagId);
            Tag tag = tagMapper.findById(tagId);
            if (tag != null) {
                tagMapper.updateRecipeCount(tagId, tag.getRecipeCount() + 1);
            }
        }
        
        invalidateCache();
    }
    
    @Transactional
    public void removeTagsFromRecipe(Long recipeId) {
        List<Long> tagIds = recipeTagMapper.findTagIdsByRecipeId(recipeId);
        
        recipeTagMapper.deleteByRecipeId(recipeId);
        
        for (Long tagId : tagIds) {
            Tag tag = tagMapper.findById(tagId);
            if (tag != null && tag.getRecipeCount() > 0) {
                tagMapper.updateRecipeCount(tagId, tag.getRecipeCount() - 1);
            }
        }
        
        invalidateCache();
    }
    
    public Tag createTag(String name) {
        Tag existing = tagMapper.findByName(name);
        if (existing != null) {
            return existing;
        }
        
        Tag tag = new Tag();
        tag.setName(name);
        tagMapper.insert(tag);
        
        invalidateCache();
        
        return tag;
    }
    
    private void invalidateCache() {
        redisTemplate.delete(TAG_LIST_CACHE);
        redisTemplate.delete(TAG_POPULAR_CACHE + ":10");
        redisTemplate.delete(TAG_POPULAR_CACHE + ":20");
    }
}
