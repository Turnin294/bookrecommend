package com.bookrecommend.service.impl;

import com.bookrecommend.dto.ChangePasswordRequest;
import com.bookrecommend.dto.UpdateUserRequest;
import com.bookrecommend.entity.User;
import com.bookrecommend.entity.UserProfile;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.mapper.UserProfileMapper;
import com.bookrecommend.service.UserService;
import com.bookrecommend.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserVO getCurrentUserInfo(String username) {
        User user = userMapper.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");

        UserProfile profile = userProfileMapper.findByUserId(user.getId());
        
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setRole(user.getRole());
        
        if (profile != null) {
            vo.setInterests(profile.getInterests());
            vo.setFavoriteCategories(profile.getFavoriteCategories());
        } else {
            vo.setInterests(new ArrayList<>());
            vo.setFavoriteCategories(new ArrayList<>());
        }
        
        return vo;
    }

    @Override
    @Transactional
    public void updateUserInfo(String username, UpdateUserRequest request) {
        User user = userMapper.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");

        user.setEmail(request.getEmail());
        user.setAvatar(request.getAvatar());
        userMapper.updateInfo(user);

        UserProfile profile = userProfileMapper.findByUserId(user.getId());
        if (profile == null) {
            profile = new UserProfile();
            profile.setUserId(user.getId());
            profile.setInterests(request.getInterests());
            profile.setFavoriteCategories(request.getFavoriteCategories());
            userProfileMapper.insert(profile);
        } else {
            profile.setInterests(request.getInterests());
            profile.setFavoriteCategories(request.getFavoriteCategories());
            userProfileMapper.updateByUserId(profile);
        }
    }

    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userMapper.findByUsername(username);
        if (user == null) throw new RuntimeException("用户不存在");

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        userMapper.updatePassword(user.getId(), passwordEncoder.encode(request.getNewPassword()));
    }

    @Override
    public Map<String, Object> getUserList(String keyword, Integer page, Integer size) {
        int offset = (page - 1) * size;
        List<User> list = userMapper.findList(keyword, offset, size);
        long total = userMapper.count(keyword);

        return Map.of(
            "list", list.stream().peek(u -> u.setPassword(null)).collect(Collectors.toList()),
            "total", total
        );
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        userMapper.updateStatus(userId, status);
    }
}
