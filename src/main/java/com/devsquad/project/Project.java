package com.devsquad.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(name = "original_requirement", columnDefinition = "TEXT")
    private String originalRequirement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private ProjectStatus status;

    @Column(name = "current_phase", nullable = false, length = 128)
    private String currentPhase;

    @Column(name = "approval_required", nullable = false)
    private boolean approvalRequired;

    @Column(name = "quality_score")
    private Double qualityScore;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    protected Project() {
    }

    public Project(String originalRequirement) {
        this.id = UUID.randomUUID();
        this.originalRequirement = originalRequirement;
        this.status = ProjectStatus.CREATED;
        this.currentPhase = "REQUIREMENTS";
        this.approvalRequired = false;
    }

    public UUID getId() {
        return id;
    }

    public String getOriginalRequirement() {
        return originalRequirement;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public String getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(String currentPhase) {
        this.currentPhase = currentPhase;
    }

    public boolean isApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }
}