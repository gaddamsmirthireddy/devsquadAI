package com.devsquad.orchestration;

import com.devsquad.project.AgentTask;
import com.devsquad.project.AgentType;
import com.devsquad.project.Artifact;
import com.devsquad.project.ArtifactType;
import com.devsquad.project.Project;
import com.devsquad.project.TaskStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WorkflowPlanner {

    public AgentTask createInitialProductTask(Project project, String requirement) {
        return new AgentTask(
                project,
                AgentType.PRODUCT_MANAGER,
                TaskStatus.PENDING,
                1,
                0,
                null,
                ArtifactType.REQUIREMENTS_SPECIFICATION.name(),
                requirement
        );
    }

    public AgentTask createArchitectTask(Project project, Artifact requirementsArtifact) {
        return new AgentTask(
                project,
                AgentType.ARCHITECT,
                TaskStatus.PENDING,
                1,
                0,
                requirementsArtifact.getId(),
                ArtifactType.ARCHITECTURE_SPECIFICATION.name(),
                requirementsArtifact.getContent()
        );
    }

    public List<AgentTask> createDesignTasks(Project project, Artifact architectureArtifact) {
        List<AgentTask> tasks = new ArrayList<>();
        tasks.add(new AgentTask(
                project,
                AgentType.DATABASE,
                TaskStatus.PENDING,
                2,
                0,
                architectureArtifact.getId(),
                ArtifactType.DATABASE_SPECIFICATION.name(),
                architectureArtifact.getContent()
        ));
        tasks.add(new AgentTask(
                project,
                AgentType.API_DESIGN,
                TaskStatus.PENDING,
                2,
                0,
                architectureArtifact.getId(),
                ArtifactType.API_SPECIFICATION.name(),
                architectureArtifact.getContent()
        ));
        return tasks;
    }
}