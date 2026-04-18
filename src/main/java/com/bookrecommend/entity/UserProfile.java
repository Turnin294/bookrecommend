package com.bookrecommend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserProfile {
    private Long id;
    private Long userId;
    private List<String> interests; // 兴趣标签
    private List<Long> favoriteCategories; // 偏好分类ID
    private LocalDateTime updatedAt;
}
