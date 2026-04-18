package com.bookrecommend.service;

import com.bookrecommend.dto.ChangePasswordRequest;
import com.bookrecommend.dto.UpdateUserRequest;
import com.bookrecommend.vo.UserVO;

import java.util.Map;

public interface UserService {
    UserVO getCurrentUserInfo(String username);
    void updateUserInfo(String username, UpdateUserRequest request);
    void changePassword(String username, ChangePasswordRequest request);
    
    // 管理员方法
    Map<String, Object> getUserList(String keyword, Integer page, Integer size);
    void updateUserStatus(Long userId, Integer status);
}
