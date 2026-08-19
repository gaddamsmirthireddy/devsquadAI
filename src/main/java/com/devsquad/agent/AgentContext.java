package com.devsquad.agent;

import java.util.Map;

public record AgentContext(Map<String, Object> projectState) {
}