package com.bookrecommend.service.impl;

import com.bookrecommend.entity.Book;
import com.bookrecommend.entity.Recommendation;
import com.bookrecommend.entity.UserBehavior;
import com.bookrecommend.entity.UserProfile;
import com.bookrecommend.mapper.BookMapper;
import com.bookrecommend.mapper.RecommendationMapper;
import com.bookrecommend.mapper.UserBehaviorMapper;
import com.bookrecommend.mapper.UserProfileMapper;
import com.bookrecommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final UserBehaviorMapper behaviorMapper;
    private final RecommendationMapper recommendationMapper;
    private final UserProfileMapper profileMapper;
    private final BookMapper bookMapper;

    @Override
    @Transactional
    public void recordBehavior(UserBehavior behavior) {
        // 如果是收藏行为 (假设 2 代表收藏)
        if (behavior.getBehaviorType() != null && behavior.getBehaviorType() == 2) {
            // 先检查是否存在该记录
            UserBehavior exist = behaviorMapper.findOneByBehavior(behavior.getUserId(), behavior.getBookId(), 2);
            
            if (exist != null) {
                // 如果已经存在，说明用户是在“取消收藏”，则删除该记录
                behaviorMapper.deleteById(exist.getId());
                refreshRecommendations(behavior.getUserId());
                return;
            }
        }
        
        // 插入行为
        behaviorMapper.insert(behavior);
        
        // 实际开发中，这里可以使用 Spring Event 或 MQ 异步触发推荐刷新
        // 我们这里简化，直接同步调用刷新 (仅限小规模数据)
        refreshRecommendations(behavior.getUserId());
    }

    @Override
    public List<Map<String, Object>> getRecommendations(Long userId, Integer size) {
        List<Recommendation> recs = recommendationMapper.findByUserId(userId, size);
        
        // 如果推荐表没数据，返回热度最高的 (即全库随机/按创建时间排)
        if (recs.isEmpty()) {
            List<Book> hotBooks = bookMapper.findList(null, null, null, 1, 0, size);
            return hotBooks.stream().map(b -> {
                Map<String, Object> map = new HashMap<>();
                map.put("bookId", b.getId());
                map.put("title", b.getTitle());
                map.put("author", b.getAuthor());
                map.put("cover", b.getCover());
                map.put("score", 1.0);
                map.put("reason", "热门图书推荐");
                map.put("algorithm", "hot");
                return map;
            }).collect(Collectors.toList());
        }

        // 填充图书详细信息
        return recs.stream().map(r -> {
            Book b = bookMapper.findById(r.getBookId());
            Map<String, Object> map = new HashMap<>();
            map.put("bookId", r.getBookId());
            map.put("title", b.getTitle());
            map.put("author", b.getAuthor());
            map.put("cover", b.getCover());
            map.put("score", r.getScore());
            map.put("reason", r.getReason());
            map.put("algorithm", r.getAlgorithm());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void refreshRecommendations(Long userId) {
        // 核心：基于内容过滤 (Content-Based)
        // 1. 获取用户画像
        UserProfile profile = profileMapper.findByUserId(userId);
        if (profile == null || (profile.getInterests() == null && profile.getFavoriteCategories() == null)) {
            return;
        }

        List<String> userInterests = profile.getInterests() != null ? profile.getInterests() : new ArrayList<>();
        
        // 2. 清理旧推荐
        recommendationMapper.deleteByUserId(userId);

        // 3. 计算推荐分值 (简单逻辑：找到包含任意用户兴趣标签的所有上架图书)
        // 这里为了演示，我们查出 100 本，然后在内存算分
        List<Book> candidates = bookMapper.findList(null, null, null, 1, 0, 100);
        
        List<Recommendation> results = new ArrayList<>();
        for (Book book : candidates) {
            List<String> bookTags = book.getTags();
            if (bookTags == null) continue;

            // 计算交集数量作为分值
            long matchCount = bookTags.stream().filter(userInterests::contains).count();
            if (matchCount > 0) {
                Recommendation r = new Recommendation();
                r.setUserId(userId);
                r.setBookId(book.getId());
                r.setScore(BigDecimal.valueOf(matchCount / (double)bookTags.size()).setScale(4, BigDecimal.ROUND_HALF_UP));
                r.setReason("基于您的兴趣标签：“" + userInterests.get(0) + "”等推荐");
                r.setAlgorithm("content");
                results.add(r);
            }
        }

        // 4. 按分值降序，存入前 10 条
        results.stream()
                .sorted(Comparator.comparing(Recommendation::getScore).reversed())
                .limit(10)
                .forEach(recommendationMapper::insert);
    }
}
