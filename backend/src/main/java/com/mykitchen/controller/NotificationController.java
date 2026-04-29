package com.mykitchen.controller;

import com.mykitchen.entity.Notification;
import com.mykitchen.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "通知管理", description = "用户通知消息")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @GetMapping
    @Operation(summary = "获取用户通知列表")
    public Result<List<Notification>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        return Result.success(notificationService.getUserNotifications(userId, limit));
    }
    
    @GetMapping("/unread-count")
    @Operation(summary = "获取未读通知数量")
    public Result<Integer> getUnreadCount(@RequestParam Long userId) {
        return Result.success(notificationService.getUnreadCount(userId));
    }
    
    @PostMapping("/{id}/read")
    @Operation(summary = "标记通知为已读")
    public Result<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return Result.success(null);
    }
    
    @PostMapping("/read-all")
    @Operation(summary = "标记所有通知为已读")
    public Result<Void> markAllAsRead(@RequestParam Long userId) {
        notificationService.markAllAsRead(userId);
        return Result.success(null);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知")
    public Result<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return Result.success(null);
    }
}
