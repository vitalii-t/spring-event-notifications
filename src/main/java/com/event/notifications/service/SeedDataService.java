package com.event.notifications.service;

import com.event.notifications.domain.model.Task;
import com.event.notifications.domain.model.TaskComment;
import com.event.notifications.domain.model.Team;
import com.event.notifications.domain.model.User;
import com.event.notifications.domain.repository.TaskCommentRepository;
import com.event.notifications.domain.repository.TaskRepository;
import com.event.notifications.domain.repository.TeamRepository;
import com.event.notifications.domain.repository.UserRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedDataService implements CommandLineRunner {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;

    public SeedDataService(
            TeamRepository teamRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            TaskCommentRepository taskCommentRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
    }

    @Override
    public void run(String... args) {
        if (teamRepository.count() > 0) {
            return;
        }

        Team acme = teamRepository.save(new Team("Acme Engineering"));
        Team globex = teamRepository.save(new Team("Globex Product"));

        User alice = userRepository.save(new User(acme, "Alice Chen", "alice@acme.test", "owner"));
        User bob = userRepository.save(new User(acme, "Bob Patel", "bob@acme.test", "member"));
        User carol = userRepository.save(new User(acme, "Carol Santos", "carol@acme.test", "member"));
        User dave = userRepository.save(new User(globex, "Dave Kim", "dave@globex.test", "owner"));
        User erin = userRepository.save(new User(globex, "Erin O'Neill", "erin@globex.test", "member"));

        Task task1 = taskRepository.save(new Task(
                acme,
                alice,
                bob,
                "Ship the Q2 release notes",
                "Collect changelog entries from each squad and publish.",
                "open"));
        taskRepository.save(new Task(
                acme,
                alice,
                carol,
                "Review onboarding flow copy",
                "Sign off on the new copy before marketing links it.",
                "open"));
        taskRepository.save(new Task(
                globex,
                dave,
                erin,
                "Draft Q3 roadmap outline",
                "One-pager for next week's leadership review.",
                "open"));

        taskCommentRepository.save(new TaskComment(
                task1,
                carol,
                "I can pick up the performance section if it helps.",
                Instant.now().minusSeconds(86_400)));
    }
}
