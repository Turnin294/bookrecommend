package com.bookrecommend.controller.admin;

import com.bookrecommend.common.Result;
import com.bookrecommend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public Result<Map<String, Object>> list(@RequestParam(defaultValue = "") String keyword,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(userService.getUserList(keyword, page, size));
    }

    @PutMapping("/{userId}/status")
    public Result<Void> updateStatus(@PathVariable Long userId, @RequestBody Map<String, Integer> body) {
        userService.updateUserStatus(userId, body.get("status"));
        return Result.success();
    }
}
