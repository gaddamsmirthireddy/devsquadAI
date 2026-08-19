package com.devsquad.agent;

import java.util.UUID;

public record AgentResult(UUID artifactId, String artifactType, String content) {
}