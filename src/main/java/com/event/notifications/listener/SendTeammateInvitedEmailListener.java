package com.event.notifications.listener;

import com.event.notifications.domain.event.TeammateInvitedEvent;
import com.event.notifications.service.mail.NotificationCommand;
import com.event.notifications.service.mail.NotificationMailer;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SendTeammateInvitedEmailListener {

    private final NotificationMailer notificationMailer;

    public SendTeammateInvitedEmailListener(NotificationMailer notificationMailer) {
        this.notificationMailer = notificationMailer;
    }

    @EventListener
    public void handle(TeammateInvitedEvent event) {
        notificationMailer.send(new NotificationCommand(
                "teammate-invited",
                event.invitee().getEmail(),
                event.invitee().getName(),
                Map.of(
                        "invitee_name", event.invitee().getName(),
                        "inviter_name", event.inviter().getName(),
                        "team_name", event.invitee().getTeam().getName())));
    }
}
