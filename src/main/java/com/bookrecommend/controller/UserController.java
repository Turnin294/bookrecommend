package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.dto.ChangePasswordRequest;
import com.bookrecommend.dto.UpdateUserRequest;
import com.bookrecommend.service.UserService;
import com.bookrecommend.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public Result<UserVO> me(Principal principal) {
        return Result.success(userService.getCurrentUserInfo(principal.getName()));
    }

    @PutMapping("/me")
    public Result<Void> updateMe(Principal principal, @RequestBody UpdateUserRequest request) {
        userService.updateUserInfo(principal.getName(), request);
        return Result.success();
    }

    @PutMapping("/me/password")
    public Result<Void> changePassword(Principal principal, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(principal.getName(), request);
        return Result.success();
    }
}
