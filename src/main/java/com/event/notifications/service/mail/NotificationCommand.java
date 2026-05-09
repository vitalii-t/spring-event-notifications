package com.event.notifications.service.mail;

import java.util.Map;

public record NotificationCommand(
        String notificationKey,
        String recipientEmail,
        String recipientName,
        Map<String, Object> variables) {
}
