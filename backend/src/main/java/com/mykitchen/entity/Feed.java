package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Feed {
    private Long id;
    private Long userId;
    private String content;
    private String imageUrl;
    private LocalDateTime createTime;
    
    private String authorNickname;
    private String authorAvatar;
    private Integer likesCount;
    private Integer commentsCount;
}
