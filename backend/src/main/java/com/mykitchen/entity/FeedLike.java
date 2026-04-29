package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FeedLike {
    private Long id;
    private Long feedId;
    private Long userId;
    private LocalDateTime createTime;
}
