package com.event.notifications.domain.repository;

import com.event.notifications.domain.model.Task;
import com.event.notifications.domain.model.Team;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Override
    @EntityGraph(attributePaths = {"team", "owner", "assignee"})
    List<Task> findAll();

    @EntityGraph(attributePaths = {"team", "owner", "assignee"})
    Optional<Task> findWithDetailsById(Long id);

    List<Task> findAllByTeamOrderByIdAsc(Team team);
}
