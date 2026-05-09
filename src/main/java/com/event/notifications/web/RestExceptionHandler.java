package com.event.notifications.web;

import com.event.notifications.service.EventSimulationService;
import com.event.notifications.web.dto.CommentEventRequest;
import com.event.notifications.web.dto.InviteEventRequest;
import com.event.notifications.web.dto.TaskAssignedEventRequest;
import jakarta.persistence.EntityNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    private final EventSimulationService eventSimulationService;

    public RestExceptionHandler(EventSimulationService eventSimulationService) {
        this.eventSimulationService = eventSimulationService;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException exception) {
        return unprocessableEntity(exception.getBindingResult());
    }

    @ExceptionHandler({EntityNotFoundException.class, IllegalArgumentException.class})
    ResponseEntity<Map<String, Object>> handleDomainError(RuntimeException exception) {
        return ResponseEntity.unprocessableEntity().body(Map.of(
                "status", "error",
                "message", exception.getMessage()));
    }

    private ResponseEntity<Map<String, Object>> unprocessableEntity(BindingResult bindingResult) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        Object target = bindingResult.getTarget();
        if (target instanceof InviteEventRequest request) {
            if (!eventSimulationService.inviterExists(request.inviterEmail())) {
                errors.putIfAbsent("inviterEmail", "Inviter must exist.");
            }
            if (!eventSimulationService.inviteeEmailAvailable(request.inviteeEmail())) {
                errors.putIfAbsent("inviteeEmail", "Invitee email already exists.");
            }
        }
        if (target instanceof TaskAssignedEventRequest request) {
            if (!eventSimulationService.taskExists(request.taskId())) {
                errors.putIfAbsent("taskId", "Task must exist.");
            }
            if (!eventSimulationService.userExists(request.assignerEmail())) {
                errors.putIfAbsent("assignerEmail", "Assigner must exist.");
            }
            if (!eventSimulationService.userExists(request.assigneeEmail())) {
                errors.putIfAbsent("assigneeEmail", "Assignee must exist.");
            } else if (eventSimulationService.taskExists(request.taskId())
                    && !eventSimulationService.userBelongsToTaskTeam(request.taskId(), request.assigneeEmail())) {
                errors.putIfAbsent("assigneeEmail", "Assignee must belong to the task team.");
            }
        }
        if (target instanceof CommentEventRequest request) {
            if (!eventSimulationService.taskExists(request.taskId())) {
                errors.putIfAbsent("taskId", "Task must exist.");
            }
            if (!eventSimulationService.userExists(request.authorEmail())) {
                errors.putIfAbsent("authorEmail", "Author must exist.");
            } else if (eventSimulationService.taskExists(request.taskId())
                    && !eventSimulationService.userBelongsToTaskTeam(request.taskId(), request.authorEmail())) {
                errors.putIfAbsent("authorEmail", "Author must belong to the task team.");
            }
        }

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(Map.of(
                "status", "error",
                "errors", errors));
    }
}
