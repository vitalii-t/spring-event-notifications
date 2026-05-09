package com.event.notifications.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "activity_events")
public class ActivityEvent {

    public static final String TYPE_TEAMMATE_INVITED = "teammate_invited";
    public static final String TYPE_TASK_ASSIGNED = "task_assigned";
    public static final String TYPE_COMMENT_POSTED = "comment_posted";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(nullable = false)
    private String type;

    @Lob
    @Column(nullable = false)
    private String payloadJson;

    @Column(nullable = false)
    private Instant occurredAt;

    protected ActivityEvent() {
    }

    public ActivityEvent(Team team, User actor, String type, String payloadJson, Instant occurredAt) {
        this.team = team;
        this.actor = actor;
        this.type = type;
        this.payloadJson = payloadJson;
        this.occurredAt = occurredAt;
    }

    public Long getId() {
        return id;
    }

    public Team getTeam() {
        return team;
    }

    public User getActor() {
        return actor;
    }

    public String getType() {
        return type;
    }

    public String getPayloadJson() {
        return payloadJson;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
