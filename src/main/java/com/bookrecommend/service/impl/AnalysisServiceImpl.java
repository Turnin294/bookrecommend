package com.bookrecommend.service.impl;

import com.bookrecommend.mapper.AnalysisMapper;
import com.bookrecommend.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisMapper analysisMapper;

    @Override
    public Map<String, Object> getBehaviorAnalysis(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> counts = analysisMapper.countBehaviorByRange(startDate, endDate);
        
        // 解析行为总数
        for (Map<String, Object> item : counts) {
            Integer type = (Integer) item.get("type");
            Long count = (Long) item.get("count");
            if (type == 1) result.put("totalBrowse", count);
            if (type == 2) result.put("totalFavorite", count);
            if (type == 3) result.put("totalRating", count);
            if (type == 4) result.put("totalRead", count);
        }
        
        result.put("dailyTrend", analysisMapper.behaviorTrend(startDate, endDate));
        return result;
    }

    @Override
    public List<Map<String, Object>> getHotBooks(Integer limit) {
        return analysisMapper.hotBooks(limit);
    }

    @Override
    public List<Map<String, Object>> getCategoryDistribution() {
        return analysisMapper.categoryDistribution();
    }

    @Override
    public Map<String, Object> getRecommendAnalysis(String startDate, String endDate) {
        long totalRecommended = analysisMapper.countTotalRecommended(startDate, endDate);
        long totalClicked = analysisMapper.countTotalClicked(startDate, endDate);
        double clickRate = totalRecommended == 0 ? 0 : (double) totalClicked / totalRecommended;

        return Map.of(
            "totalRecommended", totalRecommended,
            "totalClicked", totalClicked,
            "clickRate", clickRate,
            "algorithmBreakdown", analysisMapper.algorithmBreakdown(startDate, endDate)
        );
    }
}
