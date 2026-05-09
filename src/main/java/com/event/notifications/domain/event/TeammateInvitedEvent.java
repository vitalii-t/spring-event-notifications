package com.event.notifications.domain.event;

import com.event.notifications.domain.model.User;

public record TeammateInvitedEvent(User inviter, User invitee) {
}
