package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.User;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;
    private final UserMapper userMapper;

    @GetMapping
    public Result<List<Map<String, Object>>> list(Principal principal, @RequestParam(defaultValue = "10") Integer size) {
        User user = userMapper.findByUsername(principal.getName());
        return Result.success(recommendService.getRecommendations(user.getId(), size));
    }

    @PostMapping("/refresh")
    public Result<Void> refresh(Principal principal) {
        User user = userMapper.findByUsername(principal.getName());
        recommendService.refreshRecommendations(user.getId());
        return Result.success();
    }
}
