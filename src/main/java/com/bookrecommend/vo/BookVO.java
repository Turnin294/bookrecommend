package com.bookrecommend.vo;

import com.bookrecommend.entity.Book;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BookVO extends Book {
    private String categoryName;
}
