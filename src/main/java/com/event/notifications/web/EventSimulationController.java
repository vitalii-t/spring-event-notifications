package com.event.notifications.web;

import com.event.notifications.domain.model.Task;
import com.event.notifications.domain.model.TaskComment;
import com.event.notifications.domain.model.User;
import com.event.notifications.service.EventSimulationService;
import com.event.notifications.service.digest.DigestDispatchResult;
import com.event.notifications.service.digest.DigestService;
import com.event.notifications.web.dto.CommentEventRequest;
import com.event.notifications.web.dto.DigestTriggerRequest;
import com.event.notifications.web.dto.InviteEventRequest;
import com.event.notifications.web.dto.TaskAssignedEventRequest;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class EventSimulationController {

    private final EventSimulationService eventSimulationService;
    private final DigestService digestService;

    public EventSimulationController(EventSimulationService eventSimulationService, DigestService digestService) {
        this.eventSimulationService = eventSimulationService;
        this.digestService = digestService;
    }

    @PostMapping("/events/invite")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> invite(@Valid @RequestBody InviteEventRequest request) {
        User invitee = eventSimulationService.inviteTeammate(request);
        return Map.of(
                "status", "ok",
                "inviteeId", invitee.getId(),
                "teamId", invitee.getTeam().getId());
    }

    @PostMapping("/events/task-assigned")
    public Map<String, Object> assignTask(@Valid @RequestBody TaskAssignedEventRequest request) {
        Task task = eventSimulationService.assignTask(request);
        return Map.of(
                "status", "ok",
                "taskId", task.getId(),
                "assigneeId", task.getAssignee().getId());
    }

    @PostMapping("/events/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> comment(@Valid @RequestBody CommentEventRequest request) {
        TaskComment comment = eventSimulationService.postComment(request);
        return Map.of(
                "status", "ok",
                "commentId", comment.getId(),
                "taskId", comment.getTask().getId());
    }

    @PostMapping("/digest/trigger")
    public DigestDispatchResult triggerDigest(@RequestBody(required = false) DigestTriggerRequest request) {
        return digestService.sendDigest(
                Optional.ofNullable(request).map(DigestTriggerRequest::teamId),
                Optional.ofNullable(request).map(DigestTriggerRequest::days));
    }
}
