package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Tag {
    private Long id;
    private String name;
    private Integer recipeCount;
    private LocalDateTime createTime;
}
