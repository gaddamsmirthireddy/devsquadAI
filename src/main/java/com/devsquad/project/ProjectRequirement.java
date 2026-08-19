package com.devsquad.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "project_requirements")
public class ProjectRequirement {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    @Column(name = "raw_requirement", nullable = false, length = 4000)
    private String rawRequirement;

    @Column(name = "requirements_json", length = 10000)
    private String requirementsJson;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    protected ProjectRequirement() {
    }

    public ProjectRequirement(Project project, String rawRequirement) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.rawRequirement = rawRequirement;
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public String getRawRequirement() {
        return rawRequirement;
    }

    public String getRequirementsJson() {
        return requirementsJson;
    }

    public void setRequirementsJson(String requirementsJson) {
        this.requirementsJson = requirementsJson;
    }
}