package com.event.notifications.service.digest;

public record DigestDispatchResult(int sent, int skipped, int failed, int windowDays) {
}
