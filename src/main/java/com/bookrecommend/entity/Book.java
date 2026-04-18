package com.bookrecommend.entity;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Book {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Long categoryId;
    private String description;
    private String cover;
    private String publisher;
    private LocalDate publishDate;
    private List<String> tags; // 标签数组
    private Integer stock;
    private Integer status; // 1=上架 0=下架
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
