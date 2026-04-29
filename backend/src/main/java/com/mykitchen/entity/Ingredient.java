package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Ingredient {
    private Long id;
    private Long recipeId;
    private String name;
    private String amount;
    private Integer sortOrder;
}
