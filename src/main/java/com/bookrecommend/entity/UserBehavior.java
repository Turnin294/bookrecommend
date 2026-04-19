package com.bookrecommend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserBehavior {
    private Long id;
    private Long userId;
    private Long bookId;
    private Integer behaviorType; // 1=浏览 2=收藏 3=评分 4=完读
    private Integer score; // 评分 1-5
    private String comment; // 评价内容
    private Integer duration; // 阅读时长（秒）
    private LocalDateTime createdAt;
}
