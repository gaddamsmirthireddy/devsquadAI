package com.devsquad.api;

import com.devsquad.agent.AgentResult;

import java.util.UUID;

public record ProjectStartResponse(UUID projectId,
                                   AgentResult requirementsArtifact,
                                   AgentResult architectureArtifact,
                                   String status) {
}