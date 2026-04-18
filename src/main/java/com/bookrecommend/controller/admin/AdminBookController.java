package com.bookrecommend.controller.admin;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.Book;
import com.bookrecommend.entity.BookCategory;
import com.bookrecommend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminBookController {

    private final BookService bookService;

    @GetMapping("/books")
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "") String keyword,
                                          @RequestParam(required = false) Long categoryId,
                                          @RequestParam(required = false) String tags,
                                          @RequestParam(required = false) Integer status,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        List<String> tagList = (tags != null && !tags.isEmpty()) ? Arrays.asList(tags.split(",")) : null;
        return Result.success(bookService.getBookList(keyword, categoryId, tagList, status, page, size));
    }

    @PostMapping("/books")
    public Result<Void> create(@RequestBody Book book) {
        bookService.saveBook(book);
        return Result.success();
    }

    @PutMapping("/books/{bookId}")
    public Result<Void> update(@PathVariable Long bookId, @RequestBody Book book) {
        book.setId(bookId);
        bookService.updateBook(book);
        return Result.success();
    }

    @PutMapping("/books/{bookId}/status")
    public Result<Void> status(@PathVariable Long bookId, @RequestBody Map<String, Integer> body) {
        bookService.updateBookStatus(bookId, body.get("status"));
        return Result.success();
    }

    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody BookCategory category) {
        bookService.saveCategory(category);
        return Result.success();
    }
}
