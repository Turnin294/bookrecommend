package com.bookrecommend.service;

import com.bookrecommend.entity.UserBehavior;
import java.util.List;
import java.util.Map;

public interface RecommendService {
    // 记录行为
    void recordBehavior(UserBehavior behavior);
    
    // 获取推荐
    List<Map<String, Object>> getRecommendations(Long userId, Integer size);
    
    // 刷新推荐逻辑
    void refreshRecommendations(Long userId);
}
