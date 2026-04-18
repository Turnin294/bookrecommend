package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService analysisService;

    // 普通用户也能看热门排行
    @GetMapping("/analysis/hot-books")
    public Result<List<Map<String, Object>>> hotBooks(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(analysisService.getHotBooks(limit));
    }

    // 管理员专用：行为分析
    @GetMapping("/admin/analysis/behavior")
    public Result<Map<String, Object>> behavior(@RequestParam String startDate, @RequestParam String endDate) {
        return Result.success(analysisService.getBehaviorAnalysis(startDate, endDate));
    }

    // 管理员专用：分类分布
    @GetMapping("/admin/analysis/categories")
    public Result<List<Map<String, Object>>> categories() {
        return Result.success(analysisService.getCategoryDistribution());
    }

    // 管理员专用：推荐效果统计
    @GetMapping("/admin/analysis/recommend")
    public Result<Map<String, Object>> recommend(@RequestParam String startDate, @RequestParam String endDate) {
        return Result.success(analysisService.getRecommendAnalysis(startDate, endDate));
    }
}
