package com.bookrecommend.service;

import com.bookrecommend.dto.LoginRequest;
import com.bookrecommend.dto.RegisterRequest;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginRequest request);
    Long register(RegisterRequest request);
}
