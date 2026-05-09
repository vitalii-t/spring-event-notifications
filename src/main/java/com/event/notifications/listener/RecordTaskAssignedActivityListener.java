package com.event.notifications.listener;

import com.event.notifications.domain.event.TaskAssignedEvent;
import com.event.notifications.domain.model.ActivityEvent;
import com.event.notifications.service.ActivityRecorder;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RecordTaskAssignedActivityListener {

    private final ActivityRecorder activityRecorder;

    public RecordTaskAssignedActivityListener(ActivityRecorder activityRecorder) {
        this.activityRecorder = activityRecorder;
    }

    @EventListener
    public void handle(TaskAssignedEvent event) {
        activityRecorder.record(
                event.task().getTeam(),
                event.assigner(),
                ActivityEvent.TYPE_TASK_ASSIGNED,
                Map.of(
                        "assigner_name", event.assigner().getName(),
                        "assignee_name", event.assignee().getName(),
                        "task_title", event.task().getTitle(),
                        "task_id", event.task().getId()));
    }
}
