package com.bookrecommend.service;

import java.util.List;
import java.util.Map;

public interface AnalysisService {
    Map<String, Object> getBehaviorAnalysis(String startDate, String endDate);
    List<Map<String, Object>> getHotBooks(Integer limit);
    List<Map<String, Object>> getCategoryDistribution();
    Map<String, Object> getRecommendAnalysis(String startDate, String endDate);
}
