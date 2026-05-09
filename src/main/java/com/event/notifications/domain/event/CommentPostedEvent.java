package com.event.notifications.domain.event;

import com.event.notifications.domain.model.TaskComment;

public record CommentPostedEvent(TaskComment comment) {
}
