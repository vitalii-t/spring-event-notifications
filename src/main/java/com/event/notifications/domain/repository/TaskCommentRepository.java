package com.event.notifications.domain.repository;

import com.event.notifications.domain.model.TaskComment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    @Override
    @EntityGraph(attributePaths = {"task", "task.owner", "author"})
    List<TaskComment> findAll();

    @EntityGraph(attributePaths = {"task", "task.owner", "author"})
    Optional<TaskComment> findWithDetailsById(Long id);
}
