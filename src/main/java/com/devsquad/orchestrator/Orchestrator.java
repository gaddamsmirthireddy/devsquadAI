package com.devsquad.orchestrator;

import com.devsquad.api.ProjectStartResponse;
import com.devsquad.api.ProjectStateResponse;
import com.devsquad.orchestration.WorkflowEngine;
import com.devsquad.project.Project;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class Orchestrator {

        private final WorkflowEngine workflowEngine;

        public Orchestrator(WorkflowEngine workflowEngine) {
                this.workflowEngine = workflowEngine;
        }

        public ProjectStartResponse createProject(String requirement) throws Exception {
                return workflowEngine.startProject(requirement);
        }

        public ProjectStateResponse approveProject(UUID projectId, String comment) throws Exception {
                Project project = workflowEngine.approveAndResume(projectId, comment);
                return new ProjectStateResponse(project.getId(), project.getStatus().name(), project.getCurrentPhase(), project.isApprovalRequired());
        }

        public ProjectStateResponse rejectProject(UUID projectId, String comment) throws Exception {
                Project project = workflowEngine.rejectAndStop(projectId, comment);
                return new ProjectStateResponse(project.getId(), project.getStatus().name(), project.getCurrentPhase(), project.isApprovalRequired());
        }
}