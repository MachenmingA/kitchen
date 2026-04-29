package com.mykitchen.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private Long userId;
    private String operation;
    private String method;
    private String url;
    private String ip;
    private LocalDateTime createTime;
}
