package com.bookrecommend.controller;

import com.bookrecommend.common.Result;
import com.bookrecommend.entity.User;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserMapper userMapper;

    @GetMapping("/notifications")
    public Result<Map<String, Object>> list(Principal principal,
                                          @RequestParam(required = false) Integer isRead,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "20") Integer size) {
        User user = userMapper.findByUsername(principal.getName());
        return Result.success(notificationService.getList(user.getId(), isRead, page, size));
    }

    @GetMapping("/notifications/unread-count")
    public Result<Map<String, Integer>> unreadCount(Principal principal) {
        User user = userMapper.findByUsername(principal.getName());
        return Result.success(Map.of("count", notificationService.getUnreadCount(user.getId())));
    }

    @PutMapping("/notifications/{id}/read")
    public Result<Void> read(Principal principal, @PathVariable Long id) {
        User user = userMapper.findByUsername(principal.getName());
        notificationService.markAsRead(id, user.getId());
        return Result.success();
    }

    @PutMapping("/notifications/read-all")
    public Result<Void> readAll(Principal principal) {
        User user = userMapper.findByUsername(principal.getName());
        notificationService.markAllAsRead(user.getId());
        return Result.success();
    }

    // 管理员专用接口
    @PostMapping("/admin/notifications")
    public Result<Void> send(@RequestBody Map<String, Object> body) {
        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) body.get("userIds");
        String title = (String) body.get("title");
        String content = (String) body.get("content");
        notificationService.sendSystemNotification(userIds, title, content);
        return Result.success();
    }
}
