package com.devsquad.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgentTaskRepository extends JpaRepository<AgentTask, UUID> {
    List<AgentTask> findByProjectId(UUID projectId);
    List<AgentTask> findByProjectIdAndStatus(UUID projectId, TaskStatus status);
}