package com.event.notifications.listener;

import com.event.notifications.domain.event.CommentPostedEvent;
import com.event.notifications.service.mail.NotificationCommand;
import com.event.notifications.service.mail.NotificationMailer;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SendCommentPostedEmailListener {

    private final NotificationMailer notificationMailer;

    public SendCommentPostedEmailListener(NotificationMailer notificationMailer) {
        this.notificationMailer = notificationMailer;
    }

    @EventListener
    public void handle(CommentPostedEvent event) {
        if (event.comment().getTask().getOwner().getId().equals(event.comment().getAuthor().getId())) {
            return;
        }

        notificationMailer.send(new NotificationCommand(
                "comment-posted",
                event.comment().getTask().getOwner().getEmail(),
                event.comment().getTask().getOwner().getName(),
                Map.of(
                        "owner_name", event.comment().getTask().getOwner().getName(),
                        "author_name", event.comment().getAuthor().getName(),
                        "task_title", event.comment().getTask().getTitle(),
                        "task_id", event.comment().getTask().getId(),
                        "comment_body", event.comment().getBody())));
    }
}
