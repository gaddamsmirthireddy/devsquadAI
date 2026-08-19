package com.devsquad.orchestration;

import com.devsquad.agent.AgentContext;
import com.devsquad.agent.AgentResult;
import com.devsquad.api.ProjectStartResponse;
import com.devsquad.project.Approval;
import com.devsquad.project.ApprovalAction;
import com.devsquad.project.ApprovalRepository;
import com.devsquad.project.ApprovalStatus;
import com.devsquad.project.AgentTask;
import com.devsquad.project.AgentTaskRepository;
import com.devsquad.project.AgentType;
import com.devsquad.project.Artifact;
import com.devsquad.project.ArtifactRepository;
import com.devsquad.project.ArtifactType;
import com.devsquad.project.Project;
import com.devsquad.project.ProjectEvent;
import com.devsquad.project.ProjectEventRepository;
import com.devsquad.project.ProjectEventType;
import com.devsquad.project.ProjectRepository;
import com.devsquad.project.ProjectRequirement;
import com.devsquad.project.ProjectRequirementRepository;
import com.devsquad.project.ProjectStatus;
import com.devsquad.project.TaskStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class WorkflowEngine {

    private static final String PROMPT_VERSION = "v1";
    private static final int MAX_FIX_ATTEMPTS = 3;

    private final ProjectRepository projectRepository;
    private final ProjectRequirementRepository requirementRepository;
    private final ArtifactRepository artifactRepository;
    private final AgentTaskRepository taskRepository;
    private final ApprovalRepository approvalRepository;
    private final ProjectEventRepository eventRepository;
    private final AgentRegistry agentRegistry;
    private final WorkflowPlanner workflowPlanner;
    private final ObjectMapper objectMapper;

    public WorkflowEngine(ProjectRepository projectRepository,
                          ProjectRequirementRepository requirementRepository,
                          ArtifactRepository artifactRepository,
                          AgentTaskRepository taskRepository,
                          ApprovalRepository approvalRepository,
                          ProjectEventRepository eventRepository,
                          AgentRegistry agentRegistry,
                          WorkflowPlanner workflowPlanner,
                          ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.requirementRepository = requirementRepository;
        this.artifactRepository = artifactRepository;
        this.taskRepository = taskRepository;
        this.approvalRepository = approvalRepository;
        this.eventRepository = eventRepository;
        this.agentRegistry = agentRegistry;
        this.workflowPlanner = workflowPlanner;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public ProjectStartResponse startProject(String requirement) throws Exception {
        Project project = projectRepository.save(new Project(requirement));
        requirementRepository.save(new ProjectRequirement(project, requirement));
        recordEvent(project, ProjectEventType.PROJECT_CREATED, Map.of("requirement", requirement), null);

        taskRepository.save(workflowPlanner.createInitialProductTask(project, requirement));
        processReadyTasks(project.getId());

        Project refreshed = projectRepository.findById(project.getId()).orElseThrow();
        Optional<Artifact> requirementsArtifact = artifactRepository.findByProjectId(project.getId()).stream()
                .filter(artifact -> artifact.getArtifactType() == ArtifactType.REQUIREMENTS_SPECIFICATION)
                .findFirst();
        Optional<Artifact> architectureArtifact = artifactRepository.findByProjectId(project.getId()).stream()
                .filter(artifact -> artifact.getArtifactType() == ArtifactType.ARCHITECTURE_SPECIFICATION)
                .findFirst();
        return new ProjectStartResponse(
                refreshed.getId(),
                requirementsArtifact.map(a -> new AgentResult(a.getId(), a.getArtifactType().name(), a.getContent())).orElse(null),
                architectureArtifact.map(a -> new AgentResult(a.getId(), a.getArtifactType().name(), a.getContent())).orElse(null),
                refreshed.getStatus().name()
        );
    }

    @Transactional
    public void processReadyTasks(UUID projectId) throws Exception {
        while (true) {
            Project project = projectRepository.findById(projectId).orElseThrow();
            if (project.isApprovalRequired()) {
                return;
            }
            Optional<AgentTask> nextTask = taskRepository.findByProjectId(projectId).stream()
                    .filter(task -> task.getStatus() == TaskStatus.PENDING)
                    .sorted(Comparator.comparing(AgentTask::getCreatedAt).thenComparingInt(AgentTask::getPriority))
                    .findFirst();
            if (nextTask.isEmpty()) {
                return;
            }
            executeTask(nextTask.get());
        }
    }

    private void executeTask(AgentTask task) throws Exception {
        Project project = task.getProject();
        if (project.isApprovalRequired()) {
            return;
        }
        task.markStarted();
        taskRepository.save(task);

        AgentResult input = null;
        if (task.getInputArtifactId() != null) {
            Artifact source = artifactRepository.findById(task.getInputArtifactId()).orElseThrow();
            input = new AgentResult(source.getId(), source.getArtifactType().name(), source.getContent());
        }
        AgentContext context = new AgentContext(Map.of(
                "projectId", project.getId(),
                "taskId", task.getId(),
                "agentType", task.getAgentType().name()
        ));

        try {
            @SuppressWarnings("unchecked")
            com.devsquad.agent.Agent<Object, AgentResult> agent = (com.devsquad.agent.Agent<Object, AgentResult>) agentRegistry.get(task.getAgentType());
            Object agentInput = task.getAgentType() == AgentType.PRODUCT_MANAGER ? task.getInputPayload() : input;
            AgentResult output = agent.execute(agentInput, context);

            ArtifactType artifactType = ArtifactType.valueOf(task.getExpectedOutputType());
            Artifact artifact = artifactRepository.save(new Artifact(
                    project,
                    artifactType,
                    task.getAgentType().name(),
                    PROMPT_VERSION,
                    output.content()
            ));
            task.markCompleted(artifact.getId());
            taskRepository.save(task);

            afterTaskCompleted(project, task, artifact);
        } catch (Exception ex) {
            handleTaskFailure(project, task, ex);
        }
    }

    private void handleTaskFailure(Project project, AgentTask task, Exception ex) throws Exception {
        task.incrementRetryCount();
        String errorMessage = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
        if (task.getRetryCount() < MAX_FIX_ATTEMPTS) {
            task.markRetryScheduled(errorMessage);
            taskRepository.save(task);
            recordEvent(project,
                    ProjectEventType.RETRY_SCHEDULED,
                    Map.of("taskId", task.getId(), "retryCount", task.getRetryCount(), "error", errorMessage),
                    task.getId());
            return;
        }

        task.markFailed(errorMessage);
        taskRepository.save(task);
        project.setApprovalRequired(true);
        project.setStatus(ProjectStatus.WAITING_FOR_APPROVAL);
        project.setCurrentPhase("HUMAN_APPROVAL");
        projectRepository.save(project);
        approvalRepository.save(new Approval(project, ApprovalAction.RETRY_TASK, errorMessage));
        recordEvent(project,
                ProjectEventType.TASK_FAILED,
                Map.of("taskId", task.getId(), "error", errorMessage, "retryCount", task.getRetryCount()),
                task.getId());
        recordEvent(project,
                ProjectEventType.PROJECT_WAITING_FOR_APPROVAL,
                Map.of("projectId", project.getId(), "taskId", task.getId()),
                task.getId());
    }

    @Transactional
    public Project approveAndResume(UUID projectId, String comment) throws Exception {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Approval approval = approvalRepository
                .findFirstByProjectIdAndStatusOrderByCreatedAtDesc(projectId, ApprovalStatus.PENDING)
                .orElseThrow();
        approval.approve(comment);
        approvalRepository.save(approval);

        List<AgentTask> failedTasks = taskRepository.findByProjectIdAndStatus(projectId, TaskStatus.FAILED);
        failedTasks.forEach(AgentTask::reopenForManualRetry);
        taskRepository.saveAll(failedTasks);

        project.setApprovalRequired(false);
        project.setStatus(ProjectStatus.FAILED);
        project.setCurrentPhase("RECOVERY");
        projectRepository.save(project);
        recordEvent(project, ProjectEventType.APPROVAL_GRANTED, Map.of("projectId", projectId), null);

        processReadyTasks(projectId);
        return projectRepository.findById(projectId).orElseThrow();
    }

    @Transactional
    public Project rejectAndStop(UUID projectId, String comment) throws Exception {
        Project project = projectRepository.findById(projectId).orElseThrow();
        Approval approval = approvalRepository
                .findFirstByProjectIdAndStatusOrderByCreatedAtDesc(projectId, ApprovalStatus.PENDING)
                .orElseThrow();
        approval.reject(comment);
        approvalRepository.save(approval);
        project.setApprovalRequired(false);
        project.setStatus(ProjectStatus.FAILED);
        project.setCurrentPhase("REJECTED");
        projectRepository.save(project);
        recordEvent(project, ProjectEventType.APPROVAL_REJECTED, Map.of("projectId", projectId), null);
        return project;
    }

    private void afterTaskCompleted(Project project, AgentTask task, Artifact artifact) throws Exception {
        switch (task.getAgentType()) {
            case PRODUCT_MANAGER -> {
                ProjectRequirement requirement = requirementRepository.findByProjectId(project.getId()).orElseThrow();
                requirement.setRequirementsJson(artifact.getContent());
                requirementRepository.save(requirement);
                project.setStatus(ProjectStatus.REQUIREMENTS_COMPLETED);
                project.setCurrentPhase("ARCHITECTURE");
                projectRepository.save(project);
                recordEvent(project, ProjectEventType.REQUIREMENTS_COMPLETED, Map.of("artifactId", artifact.getId()), task.getId());
                taskRepository.save(workflowPlanner.createArchitectTask(project, artifact));
            }
            case ARCHITECT -> {
                project.setStatus(ProjectStatus.ARCHITECTURE_COMPLETED);
                project.setCurrentPhase("DESIGN");
                projectRepository.save(project);
                recordEvent(project, ProjectEventType.ARCHITECTURE_COMPLETED, Map.of("artifactId", artifact.getId()), task.getId());
                taskRepository.saveAll(workflowPlanner.createDesignTasks(project, artifact));
            }
            case DATABASE -> {
                recordEvent(project, ProjectEventType.DATABASE_DESIGN_COMPLETED, Map.of("artifactId", artifact.getId()), task.getId());
                maybeCompleteProject(project);
            }
            case API_DESIGN -> {
                recordEvent(project, ProjectEventType.API_DESIGN_COMPLETED, Map.of("artifactId", artifact.getId()), task.getId());
                maybeCompleteProject(project);
            }
        }
    }

    private void maybeCompleteProject(Project project) throws Exception {
        boolean allDesignTasksComplete = taskRepository.findByProjectId(project.getId()).stream()
                .filter(task -> task.getAgentType() == AgentType.DATABASE || task.getAgentType() == AgentType.API_DESIGN)
                .allMatch(task -> task.getStatus() == TaskStatus.COMPLETED);
        if (allDesignTasksComplete) {
            project.setStatus(ProjectStatus.READY_FOR_IMPLEMENTATION);
            project.setCurrentPhase("IMPLEMENTATION");
            projectRepository.save(project);
            recordEvent(project, ProjectEventType.PROJECT_READY_FOR_IMPLEMENTATION, Map.of("projectId", project.getId()), null);
        }
    }

    private void recordEvent(Project project, ProjectEventType eventType, Map<String, Object> payload, UUID sourceTaskId) throws Exception {
        eventRepository.save(new ProjectEvent(project, eventType, objectMapper.writeValueAsString(payload), sourceTaskId));
    }
}