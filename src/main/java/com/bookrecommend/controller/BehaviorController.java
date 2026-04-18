package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.User;
import com.bookrecommend.entity.UserBehavior;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/behavior")
@RequiredArgsConstructor
public class BehaviorController {

    private final RecommendService recommendService;
    private final UserMapper userMapper;

    @PostMapping
    public Result<Void> record(Principal principal, @RequestBody UserBehavior behavior) {
        User user = userMapper.findByUsername(principal.getName());
        behavior.setUserId(user.getId());
        recommendService.recordBehavior(behavior);
        return Result.success();
    }
}
