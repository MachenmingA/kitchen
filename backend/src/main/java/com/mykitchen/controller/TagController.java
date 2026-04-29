package com.mykitchen.controller;

import com.mykitchen.entity.Tag;
import com.mykitchen.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "标签管理", description = "食谱标签的增删改查")
public class TagController {
    
    private final TagService tagService;
    
    @GetMapping
    @Operation(summary = "获取所有标签")
    public Result<List<Tag>> getAllTags() {
        return Result.success(tagService.getAllTags());
    }
    
    @GetMapping("/popular")
    @Operation(summary = "获取热门标签")
    public Result<List<Tag>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(tagService.getPopularTags(limit));
    }
    
    @GetMapping("/recipe/{recipeId}")
    @Operation(summary = "获取食谱的标签")
    public Result<List<Tag>> getTagsByRecipe(@PathVariable Long recipeId) {
        return Result.success(tagService.getTagsByRecipeId(recipeId));
    }
    
    @PostMapping
    @Operation(summary = "创建标签")
    public Result<Tag> createTag(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            return Result.error("标签名称不能为空");
        }
        Tag tag = tagService.createTag(name.trim());
        return Result.success(tag);
    }
}
