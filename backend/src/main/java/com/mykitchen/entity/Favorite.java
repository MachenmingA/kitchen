package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Long id;
    private Long userId;
    private Long recipeId;
    private LocalDateTime createTime;
}
