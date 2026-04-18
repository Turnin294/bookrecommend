package com.bookrecommend.service.impl;

import com.bookrecommend.entity.Notification;
import com.bookrecommend.entity.User;
import com.bookrecommend.mapper.NotificationMapper;
import com.bookrecommend.mapper.UserMapper;
import com.bookrecommend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    @Override
    public Map<String, Object> getList(Long userId, Integer isRead, Integer page, Integer size) {
        int offset = (page - 1) * size;
        return Map.of("list", notificationMapper.findList(userId, isRead, offset, size));
    }

    @Override
    public int getUnreadCount(Long userId) {
        return notificationMapper.countUnread(userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        notificationMapper.markAsRead(notificationId, userId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationMapper.markAllAsRead(userId);
    }

    @Override
    @Transactional
    public void sendSystemNotification(List<Long> userIds, String title, String content) {
        if (userIds == null || userIds.isEmpty()) {
            // 广播：给所有用户发（这里为了简单，查出所有 ID。大规模系统应使用专门的广播机制）
            // 假设我们直接查 user 表
            // 由于 UserMapper 没写获取所有 ID，我这里假设用 0 作为系统广播
            // 或者先查出前 500 个用户演示
            List<User> all = userMapper.findList("", 0, 500);
            for (User u : all) {
                sendToUser(u.getId(), title, content);
            }
        } else {
            for (Long uid : userIds) {
                sendToUser(uid, title, content);
            }
        }
    }

    private void sendToUser(Long userId, String title, String content) {
        Notification n = new Notification();
        n.setUserId(userId);
        n.setTitle(title);
        n.setContent(content);
        n.setType(1); // 系统通知
        notificationMapper.insert(n);
    }
}
