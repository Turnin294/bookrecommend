package com.bookrecommend.service;

import com.bookrecommend.entity.Book;
import com.bookrecommend.entity.BookCategory;
import com.bookrecommend.vo.BookVO;

import java.util.List;
import java.util.Map;

public interface BookService {
    // 获取分类树
    List<Map<String, Object>> getCategoryTree();
    
    // 获取图书分页列表 (统一参数签名)
    Map<String, Object> getBookList(String keyword, Long categoryId, List<String> tags, Integer status, Integer page, Integer size);
    
    // 获取图书详情
    BookVO getBookDetail(Long bookId);

    // 管理员：保存图书
    void saveBook(Book book);
    
    // 管理员：更新图书
    void updateBook(Book book);
    
    // 管理员：更新图书状态
    void updateBookStatus(Long bookId, Integer status);
    
    // 管理员：保存分类
    void saveCategory(BookCategory category);
}
