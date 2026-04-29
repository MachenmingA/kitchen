package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Step {
    private Long id;
    private Long recipeId;
    private Integer stepNumber;
    private String content;
    private String imageUrl;
}
