package com.event.notifications.listener;

import com.event.notifications.domain.event.TaskAssignedEvent;
import com.event.notifications.service.mail.NotificationCommand;
import com.event.notifications.service.mail.NotificationMailer;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SendTaskAssignedEmailListener {

    private final NotificationMailer notificationMailer;

    public SendTaskAssignedEmailListener(NotificationMailer notificationMailer) {
        this.notificationMailer = notificationMailer;
    }

    @EventListener
    public void handle(TaskAssignedEvent event) {
        notificationMailer.send(new NotificationCommand(
                "task-assigned",
                event.assignee().getEmail(),
                event.assignee().getName(),
                Map.of(
                        "assignee_name", event.assignee().getName(),
                        "assigner_name", event.assigner().getName(),
                        "task_title", event.task().getTitle(),
                        "task_description", event.task().getDescription(),
                        "task_id", event.task().getId())));
    }
}
