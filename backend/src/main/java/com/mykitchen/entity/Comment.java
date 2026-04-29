package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private Long recipeId;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private String content;
    private LocalDateTime createTime;
}
