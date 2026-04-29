package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Rating {
    private Long id;
    private Long recipeId;
    private Long userId;
    private Integer score;
    private LocalDateTime createTime;
}
