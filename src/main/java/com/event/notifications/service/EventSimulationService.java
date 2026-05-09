package com.event.notifications.service;

import com.event.notifications.domain.event.CommentPostedEvent;
import com.event.notifications.domain.event.TaskAssignedEvent;
import com.event.notifications.domain.event.TeammateInvitedEvent;
import com.event.notifications.domain.model.Task;
import com.event.notifications.domain.model.TaskComment;
import com.event.notifications.domain.model.Team;
import com.event.notifications.domain.model.User;
import com.event.notifications.domain.repository.TaskCommentRepository;
import com.event.notifications.domain.repository.TaskRepository;
import com.event.notifications.domain.repository.UserRepository;
import com.event.notifications.web.dto.CommentEventRequest;
import com.event.notifications.web.dto.InviteEventRequest;
import com.event.notifications.web.dto.TaskAssignedEventRequest;
import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.Locale;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EventSimulationService {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final ApplicationEventPublisher eventPublisher;

    public EventSimulationService(
            UserRepository userRepository,
            TaskRepository taskRepository,
            TaskCommentRepository taskCommentRepository,
            ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public User inviteTeammate(InviteEventRequest request) {
        User inviter = findUserByEmail(request.inviterEmail());
        User invitee = userRepository.save(new User(
                inviter.getTeam(),
                request.inviteeName().trim(),
                normalizeEmail(request.inviteeEmail()),
                "member"));
        eventPublisher.publishEvent(new TeammateInvitedEvent(inviter, invitee));
        return invitee;
    }

    @Transactional
    public Task assignTask(TaskAssignedEventRequest request) {
        Task task = findTask(request.taskId());
        User assigner = findUserByEmail(request.assignerEmail());
        User assignee = findUserByEmail(request.assigneeEmail());
        validateSameTeam(task.getTeam(), assigner, "Assigner must belong to the task team.");
        validateSameTeam(task.getTeam(), assignee, "Assignee must belong to the task team.");
        task.assignTo(assignee);
        Task savedTask = taskRepository.save(task);
        eventPublisher.publishEvent(new TaskAssignedEvent(savedTask, assigner, assignee));
        return savedTask;
    }

    @Transactional
    public TaskComment postComment(CommentEventRequest request) {
        Task task = findTask(request.taskId());
        User author = findUserByEmail(request.authorEmail());
        validateSameTeam(task.getTeam(), author, "Author must belong to the task team.");
        TaskComment comment = taskCommentRepository.save(new TaskComment(
                task,
                author,
                request.body().trim(),
                Instant.now()));
        TaskComment hydratedComment = taskCommentRepository.findWithDetailsById(comment.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment not found after save"));
        eventPublisher.publishEvent(new CommentPostedEvent(hydratedComment));
        return hydratedComment;
    }

    public boolean inviterExists(String email) {
        return userExists(email);
    }

    public boolean userExists(String email) {
        return email != null && userRepository.findByEmailIgnoreCase(normalizeEmail(email)).isPresent();
    }

    public boolean inviteeEmailAvailable(String email) {
        return email != null && !userRepository.existsByEmailIgnoreCase(normalizeEmail(email));
    }

    public boolean taskExists(Long taskId) {
        return taskId != null && taskRepository.existsById(taskId);
    }

    public boolean userBelongsToTaskTeam(Long taskId, String email) {
        if (taskId == null || email == null || email.isBlank()) {
            return true;
        }
        return taskRepository.findWithDetailsById(taskId)
                .flatMap(task -> userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                        .map(user -> task.getTeam().getId().equals(user.getTeam().getId())))
                .orElse(false);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + email));
    }

    private Task findTask(Long taskId) {
        return taskRepository.findWithDetailsById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found: " + taskId));
    }

    private void validateSameTeam(Team team, User user, String message) {
        if (!team.getId().equals(user.getTeam().getId())) {
            throw new IllegalArgumentException(message);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
