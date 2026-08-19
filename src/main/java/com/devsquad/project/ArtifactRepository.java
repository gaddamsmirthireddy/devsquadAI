package com.devsquad.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ArtifactRepository extends JpaRepository<Artifact, UUID> {
    List<Artifact> findByProjectId(UUID projectId);
    Optional<Artifact> findTopByProjectIdAndArtifactTypeOrderByCreatedAtDesc(UUID projectId, ArtifactType artifactType);
}