package com.event.notifications.service.mail;

public interface NotificationMailer {

    boolean send(NotificationCommand command);
}
