package com.event.notifications.web.view;

import com.event.notifications.domain.repository.ActivityEventRepository;
import com.event.notifications.domain.repository.TaskCommentRepository;
import com.event.notifications.domain.repository.TaskRepository;
import com.event.notifications.domain.repository.TeamRepository;
import com.event.notifications.domain.repository.UserRepository;
import java.time.Instant;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final ActivityEventRepository activityEventRepository;

    public DashboardController(
            TeamRepository teamRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            TaskCommentRepository taskCommentRepository,
            ActivityEventRepository activityEventRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.activityEventRepository = activityEventRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("teams", teamRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("comments", taskCommentRepository.findAll());
        model.addAttribute("activityEvents", activityEventRepository.findAllByOccurredAtGreaterThanEqualOrderByOccurredAtAsc(
                Instant.now().minusSeconds(14L * 24 * 60 * 60)));
        return "index";
    }
}
