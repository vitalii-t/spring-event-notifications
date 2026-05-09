package com.event.notifications.listener;

import com.event.notifications.domain.event.TeammateInvitedEvent;
import com.event.notifications.domain.model.ActivityEvent;
import com.event.notifications.service.ActivityRecorder;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RecordTeammateInvitedActivityListener {

    private final ActivityRecorder activityRecorder;

    public RecordTeammateInvitedActivityListener(ActivityRecorder activityRecorder) {
        this.activityRecorder = activityRecorder;
    }

    @EventListener
    public void handle(TeammateInvitedEvent event) {
        activityRecorder.record(
                event.invitee().getTeam(),
                event.inviter(),
                ActivityEvent.TYPE_TEAMMATE_INVITED,
                Map.of(
                        "inviter_name", event.inviter().getName(),
                        "invitee_name", event.invitee().getName(),
                        "invitee_email", event.invitee().getEmail(),
                        "team_name", event.invitee().getTeam().getName()));
    }
}
