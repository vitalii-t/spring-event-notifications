package com.event.notifications.service.digest;

import com.event.notifications.config.NotificationProperties;
import com.event.notifications.domain.model.ActivityEvent;
import com.event.notifications.domain.model.Team;
import com.event.notifications.domain.model.User;
import com.event.notifications.domain.repository.ActivityEventRepository;
import com.event.notifications.domain.repository.TeamRepository;
import com.event.notifications.domain.repository.UserRepository;
import com.event.notifications.service.mail.NotificationCommand;
import com.event.notifications.service.mail.NotificationMailer;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DigestService {

    private static final Logger log = LoggerFactory.getLogger(DigestService.class);

    private final ActivityEventRepository activityEventRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final NotificationMailer notificationMailer;
    private final NotificationProperties properties;
    private final ObjectMapper objectMapper;

    public DigestService(
            ActivityEventRepository activityEventRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            NotificationMailer notificationMailer,
            NotificationProperties properties,
            ObjectMapper objectMapper) {
        this.activityEventRepository = activityEventRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.notificationMailer = notificationMailer;
        this.properties = properties;
        this.objectMapper = objectMapper;
    }

    public DigestDispatchResult sendDigest(Optional<Long> teamId, Optional<Integer> daysOverride) {
        int days = daysOverride.orElse(properties.getDigestWindowDays());
        Instant since = Instant.now().minus(days, ChronoUnit.DAYS);
        List<Team> teams = teamId
                .flatMap(teamRepository::findById)
                .map(List::of)
                .orElseGet(teamRepository::findAll);

        int sent = 0;
        int skipped = 0;
        int failed = 0;

        for (Team team : teams) {
            List<ActivityEvent> events = activityEventRepository
                    .findAllByTeamAndOccurredAtGreaterThanEqualOrderByOccurredAtAsc(team, since);
            if (events.isEmpty()) {
                skipped++;
                continue;
            }

            Optional<User> owner = userRepository.findByTeamAndRoleIgnoreCase(team, "owner");
            if (owner.isEmpty()) {
                skipped++;
                continue;
            }

            boolean success = notificationMailer.send(new NotificationCommand(
                    "weekly-digest",
                    owner.get().getEmail(),
                    owner.get().getName(),
                    buildDigestVariables(owner.get(), team, days, events)));

            if (success) {
                sent++;
            } else {
                failed++;
                log.warn("Digest delivery failed for team {}", team.getName());
            }
        }

        return new DigestDispatchResult(sent, skipped, failed, days);
    }

    private Map<String, Object> buildDigestVariables(User owner, Team team, int days, List<ActivityEvent> events) {
        List<Map<String, Object>> eventPayloads = new ArrayList<>();
        for (ActivityEvent event : events) {
            Map<String, Object> payload = deserializePayload(event.getPayloadJson());
            Map<String, Object> digestEvent = new LinkedHashMap<>();
            digestEvent.put("type", event.getType());
            digestEvent.put("type_label", toTypeLabel(event.getType()));
            digestEvent.put("occurred_at", event.getOccurredAt().toString());
            digestEvent.put("payload", payload);
            eventPayloads.add(digestEvent);
        }

        Map<String, Object> variables = new LinkedHashMap<>();
        variables.put("owner_name", owner.getName());
        variables.put("team_name", team.getName());
        variables.put("window_days", days);
        variables.put("event_count", events.size());
        variables.put("events", eventPayloads);
        return variables;
    }

    private Map<String, Object> deserializePayload(String payloadJson) {
        try {
            return objectMapper.readValue(payloadJson, new TypeReference<>() {
            });
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to deserialize activity payload", ex);
        }
    }

    private String toTypeLabel(String type) {
        return switch (type) {
            case ActivityEvent.TYPE_TEAMMATE_INVITED -> "Teammate invited";
            case ActivityEvent.TYPE_TASK_ASSIGNED -> "Task assigned";
            case ActivityEvent.TYPE_COMMENT_POSTED -> "Comment posted";
            default -> type.replace('-', ' ');
        };
    }
}
