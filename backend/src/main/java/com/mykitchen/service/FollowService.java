package com.mykitchen.service;

import com.mykitchen.entity.Follow;
import com.mykitchen.entity.User;
import com.mykitchen.mapper.FollowMapper;
import com.mykitchen.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FollowService {
    
    private final FollowMapper followMapper;
    private final UserMapper userMapper;
    
    public void follow(Long followerId, Long followingId) {
        if (followerId.equals(followingId)) {
            throw new RuntimeException("不能关注自己");
        }
        
        Follow existing = followMapper.find(followerId, followingId);
        if (existing != null) {
            throw new RuntimeException("已经关注过了");
        }
        
        Follow follow = new Follow();
        follow.setFollowerId(followerId);
        follow.setFollowingId(followingId);
        followMapper.insert(follow);
    }
    
    public void unfollow(Long followerId, Long followingId) {
        followMapper.delete(followerId, followingId);
    }
    
    public boolean isFollowing(Long followerId, Long followingId) {
        return followMapper.find(followerId, followingId) != null;
    }
    
    public Map<String, Object> getFollowStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("followingCount", followMapper.countFollowing(userId));
        stats.put("followersCount", followMapper.countFollowers(userId));
        return stats;
    }
    
    public List<User> getFollowingList(Long userId) {
        List<Long> followingIds = followMapper.findFollowingIds(userId);
        return followingIds.stream()
                .map(userMapper::findById)
                .filter(user -> user != null)
                .peek(user -> user.setPassword(null))
                .toList();
    }
    
    public List<User> getFollowersList(Long userId) {
        List<Follow> followers = followMapper.findFollowers(userId);
        return followers.stream()
                .map(f -> userMapper.findById(f.getFollowerId()))
                .filter(user -> user != null)
                .peek(user -> user.setPassword(null))
                .toList();
    }
}
