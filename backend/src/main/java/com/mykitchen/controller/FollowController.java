package com.mykitchen.controller;

import com.mykitchen.entity.User;
import com.mykitchen.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "关注管理", description = "用户关注功能")
public class FollowController {
    
    private final FollowService followService;
    
    @PostMapping
    @Operation(summary = "关注用户")
    public Result<Void> follow(@RequestBody Map<String, Long> params) {
        Long followerId = params.get("followerId");
        Long followingId = params.get("followingId");
        
        if (followerId == null || followingId == null) {
            return Result.error("参数错误");
        }
        
        try {
            followService.follow(followerId, followingId);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @DeleteMapping
    @Operation(summary = "取消关注")
    public Result<Void> unfollow(@RequestParam Long followerId, @RequestParam Long followingId) {
        followService.unfollow(followerId, followingId);
        return Result.success(null);
    }
    
    @GetMapping("/check")
    @Operation(summary = "检查是否关注")
    public Result<Boolean> isFollowing(@RequestParam Long followerId, @RequestParam Long followingId) {
        boolean following = followService.isFollowing(followerId, followingId);
        return Result.success(following);
    }
    
    @GetMapping("/stats/{userId}")
    @Operation(summary = "获取关注统计数据")
    public Result<Map<String, Object>> getFollowStats(@PathVariable Long userId) {
        return Result.success(followService.getFollowStats(userId));
    }
    
    @GetMapping("/following/{userId}")
    @Operation(summary = "获取关注列表")
    public Result<List<User>> getFollowingList(@PathVariable Long userId) {
        return Result.success(followService.getFollowingList(userId));
    }
    
    @GetMapping("/followers/{userId}")
    @Operation(summary = "获取粉丝列表")
    public Result<List<User>> getFollowersList(@PathVariable Long userId) {
        return Result.success(followService.getFollowersList(userId));
    }
}
