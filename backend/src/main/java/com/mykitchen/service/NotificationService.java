package com.mykitchen.service;

import com.mykitchen.entity.Notification;
import com.mykitchen.mapper.NotificationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    private final NotificationMapper notificationMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String UNREAD_COUNT_PREFIX = "notification:unread:";
    private static final long CACHE_EXPIRE_MINUTES = 5;
    
    public Notification createNotification(Long userId, String type, String title, String content) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(0);
        
        notificationMapper.insert(notification);
        
        invalidateCache(userId);
        
        return notification;
    }
    
    public List<Notification> getUserNotifications(Long userId, int limit) {
        return notificationMapper.findByUserId(userId, (long) limit);
    }
    
    public int getUnreadCount(Long userId) {
        String cacheKey = UNREAD_COUNT_PREFIX + userId;
        
        Integer cached = (Integer) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        int count = notificationMapper.countUnread(userId);
        redisTemplate.opsForValue().set(cacheKey, count, CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
        
        return count;
    }
    
    public void markAsRead(Long notificationId) {
        Notification notification = notificationMapper.findById(notificationId);
        if (notification != null) {
            notificationMapper.markAsRead(notificationId);
            invalidateCache(notification.getUserId());
        }
    }
    
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
        invalidateCache(userId);
    }
    
    public void deleteNotification(Long notificationId) {
        Notification notification = notificationMapper.findById(notificationId);
        if (notification != null) {
            notificationMapper.delete(notificationId);
            invalidateCache(notification.getUserId());
        }
    }
    
    public void notifyFavorite(Long recipeOwnerId, Long favoriteUserId, String recipeTitle) {
        createNotification(
            recipeOwnerId,
            "favorite",
            "有人收藏了你的食谱",
            "用户收藏了你的食谱《" + recipeTitle + "》"
        );
    }
    
    public void notifyComment(Long recipeOwnerId, Long commenterId, String commenterName, String recipeTitle) {
        createNotification(
            recipeOwnerId,
            "comment",
            "有人评论了你的食谱",
            commenterName + " 评论了你的食谱《" + recipeTitle + "》"
        );
    }
    
    public void notifyFollow(Long followeeId, Long followerId, String followerName) {
        createNotification(
            followeeId,
            "follow",
            "有人关注了你",
            followerName + " 关注了你"
        );
    }
    
    private void invalidateCache(Long userId) {
        redisTemplate.delete(UNREAD_COUNT_PREFIX + userId);
    }
}
