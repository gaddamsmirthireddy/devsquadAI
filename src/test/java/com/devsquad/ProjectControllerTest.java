package com.devsquad;

import com.devsquad.project.AgentTaskRepository;
import com.devsquad.project.ApprovalRepository;
import com.devsquad.project.ArtifactRepository;
import com.devsquad.project.ProjectRequirementRepository;
import com.devsquad.project.ProjectRepository;
import com.devsquad.project.ProjectStatus;
import com.devsquad.project.ProjectEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProjectControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ArtifactRepository artifactRepository;

    @Autowired
    private AgentTaskRepository agentTaskRepository;

    @Autowired
    private ProjectEventRepository projectEventRepository;

    @Autowired
    private ApprovalRepository approvalRepository;

    @Autowired
    private ProjectRequirementRepository requirementRepository;

    @BeforeEach
    void clearData() {
        approvalRepository.deleteAll();
        projectEventRepository.deleteAll();
        agentTaskRepository.deleteAll();
        artifactRepository.deleteAll();
        requirementRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    void createProjectRunsAgents() {
        String url = "http://localhost:" + port + "/api/projects";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> body = new HttpEntity<>("{\"requirement\": \"Build a Spring Boot REST API for managing customer security deposits.\"}", headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).containsKey("projectId");

        assertThat(projectRepository.count()).isEqualTo(1);
        assertThat(artifactRepository.count()).isEqualTo(4);
        assertThat(agentTaskRepository.count()).isEqualTo(4);
        assertThat(projectEventRepository.count()).isEqualTo(6);
        assertThat(projectRepository.findAll().getFirst().getStatus()).isEqualTo(ProjectStatus.READY_FOR_IMPLEMENTATION);
    }

    @Test
    void projectFailureTriggersApprovalGate() {
        String url = "http://localhost:" + port + "/api/projects";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> body = new HttpEntity<>("{\"requirement\": \"[FORCE_FAIL] Build a Spring Boot REST API for managing customer security deposits.\"}", headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(projectRepository.findAll().getFirst().getStatus()).isEqualTo(ProjectStatus.WAITING_FOR_APPROVAL);
        assertThat(projectRepository.findAll().getFirst().isApprovalRequired()).isTrue();
        assertThat(approvalRepository.count()).isEqualTo(1);
    }

    @Test
    void rejectApprovalStopsProject() {
        String createUrl = "http://localhost:" + port + "/api/projects";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> createBody = new HttpEntity<>("{\"requirement\": \"[FORCE_FAIL] Build a Spring Boot REST API for managing customer security deposits.\"}", headers);
        ResponseEntity<Map> createResponse = restTemplate.postForEntity(createUrl, createBody, Map.class);
        String projectIdString = String.valueOf(createResponse.getBody().get("projectId"));

        String rejectUrl = "http://localhost:" + port + "/api/projects/" + projectIdString + "/reject";
        HttpEntity<String> rejectBody = new HttpEntity<>("{\"comment\":\"rejecting for now\"}", headers);
        ResponseEntity<Map> rejectResponse = restTemplate.postForEntity(rejectUrl, rejectBody, Map.class);

        assertThat(rejectResponse.getStatusCode().is2xxSuccessful()).isTrue();
        UUID projectId = UUID.fromString(projectIdString);
        assertThat(projectRepository.findById(projectId)).isPresent();
        assertThat(projectRepository.findById(projectId).orElseThrow().getStatus()).isEqualTo(ProjectStatus.FAILED);
        assertThat(projectRepository.findById(projectId).orElseThrow().isApprovalRequired()).isFalse();
    }
}