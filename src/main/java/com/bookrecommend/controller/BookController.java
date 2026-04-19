package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.Book;
import com.bookrecommend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping("/books")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "") String keyword,
                                          @RequestParam(required = false) Long categoryId,
                                          @RequestParam(required = false) String tags,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        List<String> tagList = (tags != null && !tags.isEmpty()) ? Arrays.asList(tags.split(",")) : null;
        // 普通用户只能查上架状态的图书
        return Result.success(bookService.getBookList(keyword, categoryId, tagList, 1, page, size));
    }

    @GetMapping("/books/{bookId}")
    public Result<com.bookrecommend.vo.BookVO> detail(@PathVariable("bookId") Long bookId) {
        return Result.success(bookService.getBookDetail(bookId));
    }

    @GetMapping("/categories")
    public Result<List<Map<String, Object>>> categories() {
        return Result.success(bookService.getCategoryTree());
    }
}
