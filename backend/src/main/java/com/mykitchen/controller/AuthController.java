package com.mykitchen.controller;

import com.mykitchen.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户登录、注册、刷新令牌等")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        try {
            Map<String, Object> result = authService.login(username, password);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<Map<String, Object>> register(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String nickname = params.get("nickname");
        
        if (username == null || password == null) {
            return Result.error("用户名和密码不能为空");
        }
        
        if (password.length() < 6) {
            return Result.error("密码长度不能少于6位");
        }
        
        try {
            var user = authService.register(username, password, nickname);
            Map<String, Object> result = Map.of("user", user);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌")
    public Result<Map<String, Object>> refresh(@RequestBody Map<String, String> params) {
        String refreshToken = params.get("refreshToken");
        
        if (refreshToken == null) {
            return Result.error("刷新令牌不能为空");
        }
        
        try {
            Map<String, Object> result = authService.refreshToken(refreshToken);
            return Result.success(result);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            authService.logout(token);
        }
        return Result.success(null);
    }
}
