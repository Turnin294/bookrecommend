package com.bookrecommend.service;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    Map<String, Object> getList(Long userId, Integer isRead, Integer page, Integer size);
    int getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    void sendSystemNotification(List<Long> userIds, String title, String content);
}
