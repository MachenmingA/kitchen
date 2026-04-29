package com.mykitchen.service;

import com.mykitchen.entity.Feed;
import com.mykitchen.entity.User;
import com.mykitchen.mapper.FeedLikeMapper;
import com.mykitchen.mapper.FeedMapper;
import com.mykitchen.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    
    private final FeedMapper feedMapper;
    private final FeedLikeMapper feedLikeMapper;
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String FEED_FEED_PREFIX = "feed:";
    private static final long CACHE_EXPIRE_MINUTES = 5;
    
    public Feed createFeed(Long userId, String content, String imageUrl) {
        Feed feed = new Feed();
        feed.setUserId(userId);
        feed.setContent(content);
        feed.setImageUrl(imageUrl);
        
        feedMapper.insert(feed);
        
        User user = userMapper.findById(userId);
        if (user != null) {
            feed.setAuthorNickname(user.getNickname());
            feed.setAuthorAvatar(user.getAvatar());
        }
        
        invalidateFeedCache();
        
        return feed;
    }
    
    public void deleteFeed(Long feedId, Long userId) {
        Feed feed = feedMapper.findById(feedId);
        if (feed != null && feed.getUserId().equals(userId)) {
            feedMapper.delete(feedId);
            invalidateFeedCache();
        }
    }
    
    public List<Feed> getUserFeeds(Long userId) {
        return enrichFeeds(feedMapper.findByUserId(userId));
    }
    
    public List<Feed> getFollowingFeeds(Long userId) {
        List<Feed> feeds = feedMapper.findRecent(50);
        return enrichFeeds(feeds);
    }
    
    public List<Feed> getRecentFeeds(int limit) {
        return enrichFeeds(feedMapper.findRecent((long) limit));
    }
    
    public void likeFeed(Long feedId, Long userId) {
        feedLikeMapper.insert(feedId, userId);
    }
    
    public void unlikeFeed(Long feedId, Long userId) {
        feedLikeMapper.delete(feedId, userId);
    }
    
    public boolean isLiked(Long feedId, Long userId) {
        return feedLikeMapper.find(feedId, userId) != null;
    }
    
    public int getLikesCount(Long feedId) {
        return feedLikeMapper.countByFeedId(feedId);
    }
    
    private List<Feed> enrichFeeds(List<Feed> feeds) {
        return feeds.stream()
                .peek(feed -> {
                    User user = userMapper.findById(feed.getUserId());
                    if (user != null) {
                        feed.setAuthorNickname(user.getNickname());
                        feed.setAuthorAvatar(user.getAvatar());
                    }
                    feed.setLikesCount(feedLikeMapper.countByFeedId(feed.getId()));
                })
                .collect(Collectors.toList());
    }
    
    private void invalidateFeedCache() {
        // Simple cache invalidation - in production, use more sophisticated caching
    }
}
