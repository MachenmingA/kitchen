package com.mykitchen.service;

import com.mykitchen.entity.User;
import com.mykitchen.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String USER_CACHE_PREFIX = "user:";
    private static final long CACHE_EXPIRE_HOURS = 24;
    
    public User getUserById(Long id) {
        String cacheKey = USER_CACHE_PREFIX + id;
        
        User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
        if (cachedUser != null) {
            return cachedUser;
        }
        
        User user = userMapper.findById(id);
        if (user != null) {
            redisTemplate.opsForValue().set(cacheKey, user, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        
        return user;
    }
    
    public User getUserByUsername(String username) {
        return userMapper.findByUsername(username);
    }
    
    public User register(User user) {
        if (userMapper.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        userMapper.insert(user);
        return user;
    }
    
    public User updateUser(User user) {
        userMapper.update(user);
        
        String cacheKey = USER_CACHE_PREFIX + user.getId();
        redisTemplate.delete(cacheKey);
        
        User updated = userMapper.findById(user.getId());
        if (updated != null) {
            redisTemplate.opsForValue().set(cacheKey, updated, CACHE_EXPIRE_HOURS, TimeUnit.HOURS);
        }
        
        return updated;
    }
    
    public void invalidateCache(Long userId) {
        redisTemplate.delete(USER_CACHE_PREFIX + userId);
    }
}
