package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.dto.LoginRequest;
import com.bookrecommend.dto.RegisterRequest;
import com.bookrecommend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<Map<String, Long>> register(@Valid @RequestBody RegisterRequest request) {
        Long userId = authService.register(request);
        return Result.success(Map.of("userId", userId));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        Map<String, Object> data = authService.login(request);
        return Result.success(data);
    }
}
