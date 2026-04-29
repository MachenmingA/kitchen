package com.mykitchen.security;

import com.mykitchen.entity.User;
import com.mykitchen.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    private static final String TOKEN_PREFIX = "token:";
    private static final long TOKEN_EXPIRE_HOURS = 24;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            
            if (StringUtils.hasText(token) && jwtUtil.validateToken(token)) {
                String cacheKey = TOKEN_PREFIX + token;
                
                User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);
                if (cachedUser != null) {
                    request.setAttribute("currentUser", cachedUser);
                } else {
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    User user = userService.getUserById(userId);
                    if (user != null) {
                        redisTemplate.opsForValue().set(cacheKey, user, TOKEN_EXPIRE_HOURS, TimeUnit.HOURS);
                        request.setAttribute("currentUser", user);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed", e);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
