package com.bookrecommend.controller.admin;

import com.bookrecommend.common.Result;
import com.bookrecommend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import com.bookrecommend.mapper.UserBehaviorMapper;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    private final UserBehaviorMapper behaviorMapper;

    @GetMapping("/reviews")
    public Result<Map<String, Object>> reviews(@RequestParam(defaultValue = "1") Integer page,
                                              @RequestParam(defaultValue = "20") Integer size) {
        int offset = (page - 1) * size;
        return Result.success(Map.of(
            "list", behaviorMapper.findAllRatingsWithUserInfo(offset, size),
            "total", behaviorMapper.countAllRatings()
        ));
    }

    @DeleteMapping("/reviews/{id}")
    public Result<Void> deleteReview(@PathVariable Long id) {
        behaviorMapper.deleteById(id);
        return Result.success();
    }

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
