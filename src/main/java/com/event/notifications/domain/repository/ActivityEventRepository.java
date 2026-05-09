package com.event.notifications.domain.repository;

import com.event.notifications.domain.model.ActivityEvent;
import com.event.notifications.domain.model.Team;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityEventRepository extends JpaRepository<ActivityEvent, Long> {

    @EntityGraph(attributePaths = {"team", "actor"})
    List<ActivityEvent> findAllByTeamAndOccurredAtGreaterThanEqualOrderByOccurredAtAsc(Team team, Instant since);

    @EntityGraph(attributePaths = {"team", "actor"})
    List<ActivityEvent> findAllByOccurredAtGreaterThanEqualOrderByOccurredAtAsc(Instant since);
}
