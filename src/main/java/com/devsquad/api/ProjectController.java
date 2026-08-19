package com.devsquad.api;

import com.devsquad.orchestrator.Orchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final Orchestrator orchestrator;

    public ProjectController(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @PostMapping
    public ResponseEntity<ProjectStartResponse> createProject(@RequestBody Map<String, String> body) throws Exception {
        String requirement = body.get("requirement");
        return ResponseEntity.ok(orchestrator.createProject(requirement));
    }

    @PostMapping("/{projectId}/approve")
    public ResponseEntity<ProjectStateResponse> approveProject(@PathVariable("projectId") UUID projectId,
                                                               @RequestBody(required = false) ApprovalDecisionRequest body) throws Exception {
        String comment = body == null ? null : body.comment();
        return ResponseEntity.ok(orchestrator.approveProject(projectId, comment));
    }

    @PostMapping("/{projectId}/reject")
    public ResponseEntity<ProjectStateResponse> rejectProject(@PathVariable("projectId") UUID projectId,
                                                              @RequestBody(required = false) ApprovalDecisionRequest body) throws Exception {
        String comment = body == null ? null : body.comment();
        return ResponseEntity.ok(orchestrator.rejectProject(projectId, comment));
    }
}