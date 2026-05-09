package com.event.notifications.service.digest;

import java.util.Optional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeeklyDigestScheduler {

    private final DigestService digestService;

    public WeeklyDigestScheduler(DigestService digestService) {
        this.digestService = digestService;
    }

    @Scheduled(cron = "${app.notifications.digest-cron}", zone = "${app.notifications.digest-zone}")
    public void sendWeeklyDigest() {
        digestService.sendDigest(Optional.empty(), Optional.empty());
    }
}
