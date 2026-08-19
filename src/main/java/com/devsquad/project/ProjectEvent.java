package com.devsquad.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_events")
public class ProjectEvent {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 64)
    private ProjectEventType eventType;

    @Column(name = "payload_json", length = 10000)
    private String payloadJson;

    @Column(name = "source_task_id")
    private UUID sourceTaskId;

    @CreationTimestamp
    @Column(name = "occurred_at", nullable = false, updatable = false)
    private LocalDateTime occurredAt;

    protected ProjectEvent() {
    }

    public ProjectEvent(Project project, ProjectEventType eventType, String payloadJson, UUID sourceTaskId) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.eventType = eventType;
        this.payloadJson = payloadJson;
        this.sourceTaskId = sourceTaskId;
    }
}