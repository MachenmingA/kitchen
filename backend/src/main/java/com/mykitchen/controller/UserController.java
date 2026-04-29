package com.mykitchen.controller;

import com.mykitchen.entity.User;
import com.mykitchen.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/username/{username}")
    public Result<User> getByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            User newUser = userService.register(user);
            newUser.setPassword(null);
            return Result.success(newUser);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updated = userService.updateUser(user);
        updated.setPassword(null);
        return Result.success(updated);
    }
}
