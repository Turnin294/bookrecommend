package com.bookrecommend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BookCategory {
    private Long id;
    private String name;
    private Long parentId;
    private Integer sort;
    private LocalDateTime createdAt;
}
