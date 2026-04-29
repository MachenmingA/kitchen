package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Recipe {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private String difficulty;
    private Integer cookTime;
    private Integer servings;
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private Integer favoritesCount;
    private Integer viewsCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
