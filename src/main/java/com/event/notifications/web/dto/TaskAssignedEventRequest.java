package com.event.notifications.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TaskAssignedEventRequest(
        @NotNull Long taskId,
        @NotBlank @Email String assignerEmail,
        @NotBlank @Email String assigneeEmail) {
}
