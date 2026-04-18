package com.bookrecommend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notification {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Integer type; // 1=系统通知 2=推荐通知
    private Integer isRead;
    private LocalDateTime createdAt;
}
