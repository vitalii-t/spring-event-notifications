package com.event.notifications.service;

import com.event.notifications.domain.model.ActivityEvent;
import com.event.notifications.domain.model.Team;
import com.event.notifications.domain.model.User;
import com.event.notifications.domain.repository.ActivityEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ActivityRecorder {

    private final ActivityEventRepository activityEventRepository;
    private final ObjectMapper objectMapper;

    public ActivityRecorder(ActivityEventRepository activityEventRepository, ObjectMapper objectMapper) {
        this.activityEventRepository = activityEventRepository;
        this.objectMapper = objectMapper;
    }

    public ActivityEvent record(Team team, User actor, String type, Map<String, Object> payload) {
        return activityEventRepository.save(new ActivityEvent(team, actor, type, serialize(payload), Instant.now()));
    }

    private String serialize(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize activity payload", ex);
        }
    }
}
