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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "artifacts")
public class Artifact {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "artifact_type", nullable = false, length = 64)
    private ArtifactType artifactType;

    @Column(name = "created_by_agent_type", nullable = false, length = 64)
    private String createdByAgentType;

    @Column(name = "prompt_version", nullable = false, length = 64)
    private String promptVersion;

    @Column(nullable = false, length = 10000)
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    @Column(nullable = false)
    private Long version;

    protected Artifact() {
    }

    public Artifact(Project project, ArtifactType artifactType, String createdByAgentType, String promptVersion, String content) {
        this.id = UUID.randomUUID();
        this.project = project;
        this.artifactType = artifactType;
        this.createdByAgentType = createdByAgentType;
        this.promptVersion = promptVersion;
        this.content = content;
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public ArtifactType getArtifactType() {
        return artifactType;
    }

    public String getContent() {
        return content;
    }
}