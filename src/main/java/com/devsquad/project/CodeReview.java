package com.devsquad.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "code_reviews")
public class CodeReview {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(name = "critical_count", nullable = false)
    private int criticalCount;

    @Column(name = "high_count", nullable = false)
    private int highCount;

    @Column(name = "medium_count", nullable = false)
    private int mediumCount;

    @Column(name = "low_count", nullable = false)
    private int lowCount;

    @Column(nullable = false)
    private boolean passed;

    @Column(length = 10000)
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected CodeReview() {
    }

    public CodeReview(Project project,
                      int criticalCount,
                      int highCount,
                      int mediumCount,
                      int lowCount,
                      boolean passed,
                      String summary) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.criticalCount = criticalCount;
        this.highCount = highCount;
        this.mediumCount = mediumCount;
        this.lowCount = lowCount;
        this.passed = passed;
        this.summary = summary;
    }

    public UUID getId() {
        return id;
    }

    public int getCriticalCount() {
        return criticalCount;
    }

    public int getHighCount() {
        return highCount;
    }

    public boolean isPassed() {
        return passed;
    }
}