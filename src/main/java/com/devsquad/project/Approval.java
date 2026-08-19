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
@Table(name = "approvals")
public class Approval {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ApprovalAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ApprovalStatus status;

    @Column(name = "reason", length = 10000)
    private String reason;

    @Column(name = "decision_comment", length = 10000)
    private String decisionComment;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Approval() {
    }

    public Approval(Project project, ApprovalAction action, String reason) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.action = action;
        this.reason = reason;
        this.status = ApprovalStatus.PENDING;
    }

    public UUID getId() {
        return id;
    }

    public ApprovalStatus getStatus() {
        return status;
    }

    public void approve(String decisionComment) {
        this.status = ApprovalStatus.APPROVED;
        this.decisionComment = decisionComment;
        this.decidedAt = LocalDateTime.now();
    }

    public void reject(String decisionComment) {
        this.status = ApprovalStatus.REJECTED;
        this.decisionComment = decisionComment;
        this.decidedAt = LocalDateTime.now();
    }
}