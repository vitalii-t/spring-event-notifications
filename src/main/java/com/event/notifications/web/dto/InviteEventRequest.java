package com.event.notifications.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record InviteEventRequest(
        @NotBlank @Email String inviterEmail,
        @NotBlank @Size(max = 120) String inviteeName,
        @NotBlank @Email String inviteeEmail) {
}
