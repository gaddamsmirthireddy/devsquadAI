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
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "agent_tasks")
public class AgentTask {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "agent_type", nullable = false, length = 64)
    private AgentType agentType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TaskStatus status;

    @Column(nullable = false)
    private int priority;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "input_artifact_id")
    private UUID inputArtifactId;

    @Column(name = "output_artifact_id")
    private UUID outputArtifactId;

    @Column(name = "expected_output_type", nullable = false, length = 128)
    private String expectedOutputType;

    @Column(name = "input_payload", length = 10000)
    private String inputPayload;

    @Column(name = "error_message", length = 10000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    protected AgentTask() {
    }

    public AgentTask(Project project,
                     AgentType agentType,
                     TaskStatus status,
                     int priority,
                     int retryCount,
                     UUID inputArtifactId,
                     String expectedOutputType,
                     String inputPayload) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.agentType = agentType;
        this.status = status;
        this.priority = priority;
        this.retryCount = retryCount;
        this.inputArtifactId = inputArtifactId;
        this.expectedOutputType = expectedOutputType;
        this.inputPayload = inputPayload;
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public AgentType getAgentType() {
        return agentType;
    }

    public int getPriority() {
        return priority;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public UUID getInputArtifactId() {
        return inputArtifactId;
    }

    public String getExpectedOutputType() {
        return expectedOutputType;
    }

    public String getInputPayload() {
        return inputPayload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setOutputArtifactId(UUID outputArtifactId) {
        this.outputArtifactId = outputArtifactId;
    }

    public UUID getOutputArtifactId() {
        return outputArtifactId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void markStarted() {
        this.startedAt = LocalDateTime.now();
        this.status = TaskStatus.RUNNING;
    }

    public void markCompleted(UUID outputArtifactId) {
        this.outputArtifactId = outputArtifactId;
        this.completedAt = LocalDateTime.now();
        this.status = TaskStatus.COMPLETED;
    }

    public void markFailed(String errorMessage) {
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
        this.status = TaskStatus.FAILED;
    }

    public void incrementRetryCount() {
        this.retryCount = this.retryCount + 1;
    }

    public void markRetryScheduled(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = TaskStatus.PENDING;
        this.startedAt = null;
        this.completedAt = null;
    }

    public void reopenForManualRetry() {
        this.errorMessage = null;
        this.retryCount = 0;
        this.status = TaskStatus.PENDING;
        this.startedAt = null;
        this.completedAt = null;
    }
}