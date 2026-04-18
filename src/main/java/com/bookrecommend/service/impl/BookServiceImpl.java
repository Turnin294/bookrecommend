package com.bookrecommend.service.impl;

import com.bookrecommend.entity.Book;
import com.bookrecommend.entity.BookCategory;
import com.bookrecommend.mapper.BookCategoryMapper;
import com.bookrecommend.mapper.BookMapper;
import com.bookrecommend.service.BookService;
import com.bookrecommend.vo.BookVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookMapper bookMapper;
    private final BookCategoryMapper categoryMapper;

    @Override
    public List<Map<String, Object>> getCategoryTree() {
        List<BookCategory> all = categoryMapper.findAll();
        return buildTree(all, 0L);
    }

    private List<Map<String, Object>> buildTree(List<BookCategory> all, Long parentId) {
        return all.stream()
                .filter(c -> c.getParentId().equals(parentId))
                .map(c -> {
                    Map<String, Object> node = new HashMap<>();
                    node.put("id", c.getId());
                    node.put("name", c.getName());
                    node.put("children", buildTree(all, c.getId()));
                    return node;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getBookList(String keyword, Long categoryId, List<String> tags, Integer status, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<Book> list = bookMapper.findList(keyword, categoryId, tags, status, offset, size);
        long total = bookMapper.count(keyword, categoryId, tags, status);
        
        return Map.of("list", list, "total", total);
    }

    @Override
    public BookVO getBookDetail(Long bookId) {
        Book book = bookMapper.findById(bookId);
        if (book == null) throw new RuntimeException("图书不存在");
        
        BookVO vo = new BookVO();
        org.springframework.beans.BeanUtils.copyProperties(book, vo);
        
        BookCategory category = categoryMapper.findById(book.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }
        
        return vo;
    }

    @Override
    public void saveBook(Book book) {
        bookMapper.insert(book);
    }

    @Override
    public void updateBook(Book book) {
        bookMapper.update(book);
    }

    @Override
    public void updateBookStatus(Long bookId, Integer status) {
        bookMapper.updateStatus(bookId, status);
    }

    @Override
    public void saveCategory(BookCategory category) {
        categoryMapper.insert(category);
    }
}
