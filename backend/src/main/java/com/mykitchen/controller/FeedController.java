package com.mykitchen.controller;

import com.mykitchen.entity.Feed;
import com.mykitchen.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feeds")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "动态管理", description = "用户动态发布")
public class FeedController {
    
    private final FeedService feedService;
    
    @GetMapping
    @Operation(summary = "获取最新动态")
    public Result<List<Feed>> getRecentFeeds(@RequestParam(defaultValue = "20") int limit) {
        return Result.success(feedService.getRecentFeeds(limit));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户动态")
    public Result<List<Feed>> getUserFeeds(@PathVariable Long userId) {
        return Result.success(feedService.getUserFeeds(userId));
    }
    
    @PostMapping
    @Operation(summary = "发布动态")
    public Result<Feed> createFeed(@RequestBody Map<String, String> params) {
        Long userId = Long.parseLong(params.get("userId"));
        String content = params.get("content");
        String imageUrl = params.get("imageUrl");
        
        Feed feed = feedService.createFeed(userId, content, imageUrl);
        return Result.success(feed);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除动态")
    public Result<Void> deleteFeed(@PathVariable Long id, @RequestParam Long userId) {
        feedService.deleteFeed(id, userId);
        return Result.success(null);
    }
    
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞动态")
    public Result<Void> likeFeed(@PathVariable Long id, @RequestParam Long userId) {
        feedService.likeFeed(id, userId);
        return Result.success(null);
    }
    
    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞")
    public Result<Void> unlikeFeed(@PathVariable Long id, @RequestParam Long userId) {
        feedService.unlikeFeed(id, userId);
        return Result.success(null);
    }
}
