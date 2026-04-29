package com.mykitchen.service;

import com.mykitchen.entity.User;
import com.mykitchen.mapper.UserMapper;
import com.mykitchen.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    private static final String USER_ONLINE_PREFIX = "user:online:";
    
    public Map<String, Object> login(String username, String password) {
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        String accessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        
        redisTemplate.opsForValue().set(USER_ONLINE_PREFIX + user.getId(), accessToken, 24, TimeUnit.HOURS);
        
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", accessToken);
        result.put("refreshToken", refreshToken);
        result.put("user", maskPassword(user));
        result.put("expiresIn", 86400);
        
        return result;
    }
    
    public Map<String, Object> refreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new RuntimeException("无效的刷新令牌");
        }
        
        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        String newAccessToken = jwtUtil.generateToken(user.getId(), user.getUsername());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId(), user.getUsername());
        
        Map<String, Object> result = new HashMap<>();
        result.put("accessToken", newAccessToken);
        result.put("refreshToken", newRefreshToken);
        result.put("user", maskPassword(user));
        result.put("expiresIn", 86400);
        
        return result;
    }
    
    public void logout(String token) {
        try {
            Long userId = jwtUtil.getUserIdFromToken(token);
            redisTemplate.delete(USER_ONLINE_PREFIX + userId);
            redisTemplate.opsForValue().set(TOKEN_BLACKLIST_PREFIX + token, "1", 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.error("Logout failed", e);
        }
    }
    
    public User register(String username, String password, String nickname) {
        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : username);
        user.setAvatar("https://api.dicebear.com/7.x/avataaars/svg?seed=" + username);
        
        userMapper.insert(user);
        return maskPassword(user);
    }
    
    private User maskPassword(User user) {
        if (user == null) return null;
        user.setPassword(null);
        return user;
    }
}
