package com.event.notifications.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CommentEventRequest(
        @NotNull Long taskId,
        @NotBlank @Email String authorEmail,
        @NotBlank @Size(max = 2000) String body) {
}
