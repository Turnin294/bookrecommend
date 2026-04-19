package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.User;
import com.bookrecommend.entity.UserBehavior;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.mapper.UserBehaviorMapper;
import com.bookrecommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/behavior")
@RequiredArgsConstructor
public class BehaviorController {

    private final RecommendService recommendService;
    private final UserMapper userMapper;
    private final UserBehaviorMapper behaviorMapper;

    @PostMapping
    public Result<Void> record(Principal principal, @RequestBody UserBehavior behavior) {
        User user = userMapper.findByUsername(principal.getName());
        behavior.setUserId(user.getId());
        recommendService.recordBehavior(behavior);
        return Result.success();
    }

    @GetMapping("/my")
    public Result<Map<String, Object>> myBehaviors(Principal principal) {
        User user = userMapper.findByUsername(principal.getName());
        List<Map<String, Object>> favorites = behaviorMapper.findMyBehaviorsWithType(user.getId(), 2);
        List<Map<String, Object>> ratings = behaviorMapper.findMyBehaviorsWithType(user.getId(), 3);
        List<Map<String, Object>> finished = behaviorMapper.findMyBehaviorsWithType(user.getId(), 4);
        
        return Result.success(Map.of(
            "favorites", favorites,
            "ratings", ratings,
            "finished", finished
        ));
    }

    @GetMapping("/book/{bookId}/reviews")
    public Result<List<Map<String, Object>>> bookReviews(@PathVariable Long bookId) {
        return Result.success(behaviorMapper.findPublicReviewsByBookId(bookId, 20));
    }

    @GetMapping("/book/{bookId}/progress")
    public Result<Integer> getProgress(Principal principal, @PathVariable Long bookId) {
        User user = userMapper.findByUsername(principal.getName());
        UserBehavior progress = behaviorMapper.findLatestProgress(user.getId(), bookId);
        return Result.success(progress != null ? progress.getDuration() : 0);
    }
}

@Component
@RequiredArgsConstructor
class DbUpdater {
    private final JdbcTemplate jdbcTemplate;
    
    @PostConstruct
    public void update() {
        try {
            jdbcTemplate.execute("ALTER TABLE user_behavior ADD COLUMN comment VARCHAR(500) COMMENT '评价内容'");
        } catch(Exception e) {}
    }
}
