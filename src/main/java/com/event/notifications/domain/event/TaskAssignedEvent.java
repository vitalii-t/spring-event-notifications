package com.event.notifications.domain.event;

import com.event.notifications.domain.model.Task;
import com.event.notifications.domain.model.User;

public record TaskAssignedEvent(Task task, User assigner, User assignee) {
}
