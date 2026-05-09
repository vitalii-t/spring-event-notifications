package com.event.notifications.listener;

import com.event.notifications.domain.event.CommentPostedEvent;
import com.event.notifications.domain.model.ActivityEvent;
import com.event.notifications.service.ActivityRecorder;
import java.util.Map;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class RecordCommentPostedActivityListener {

    private final ActivityRecorder activityRecorder;

    public RecordCommentPostedActivityListener(ActivityRecorder activityRecorder) {
        this.activityRecorder = activityRecorder;
    }

    @EventListener
    public void handle(CommentPostedEvent event) {
        activityRecorder.record(
                event.comment().getTask().getTeam(),
                event.comment().getAuthor(),
                ActivityEvent.TYPE_COMMENT_POSTED,
                Map.of(
                        "author_name", event.comment().getAuthor().getName(),
                        "task_title", event.comment().getTask().getTitle(),
                        "task_id", event.comment().getTask().getId(),
                        "comment_excerpt", abbreviate(event.comment().getBody())));
    }

    private String abbreviate(String value) {
        return value.length() <= 80 ? value : value.substring(0, 77) + "...";
    }
}
