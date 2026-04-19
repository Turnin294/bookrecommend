package com.bookrecommend.controller.admin;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.Book;
import com.bookrecommend.entity.BookCategory;
import com.bookrecommend.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    // 核心新增：文件上传
    @PostMapping("/books/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) throws IOException {
        if (file.isEmpty()) return Result.error("文件为空");
        
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = UUID.randomUUID().toString() + extension;
        
        // 存储路径：前端的 public 目录（用于封面）
        String baseDir = type.equals("cover") ? "D:/code/java/bookrecommend/frontend/public/covers" : "D:/code/java/bookrecommend/src/main/resources/books";
        
        File dest = new File(baseDir + "/" + newFileName);
        if (!dest.getParentFile().exists()) dest.getParentFile().mkdirs();
        file.transferTo(dest);
        
        // 返回前端可以直接使用的访问路径
        return Result.success(type.equals("cover") ? "/covers/" + newFileName : newFileName);
    }

    @PostMapping("/categories")
    public Result<Void> createCategory(@RequestBody BookCategory category) {
        bookService.saveCategory(category);
        return Result.success();
    }
}
