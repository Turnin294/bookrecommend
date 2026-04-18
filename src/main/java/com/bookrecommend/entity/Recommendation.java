package com.bookrecommend.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Recommendation {
    private Long id;
    private Long userId;
    private Long bookId;
    private BigDecimal score; // 推荐分值 0~1
    private String reason;
    private String algorithm; // cf/content/hybrid
    private Integer isRead;
    private LocalDateTime createdAt;
}
