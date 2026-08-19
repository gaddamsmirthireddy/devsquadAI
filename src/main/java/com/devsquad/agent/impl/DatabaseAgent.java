package com.devsquad.agent.impl;

import com.devsquad.agent.Agent;
import com.devsquad.agent.AgentContext;
import com.devsquad.agent.AgentResult;
import com.devsquad.llm.LLMProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class DatabaseAgent implements Agent<AgentResult, AgentResult> {
    private final UUID id = UUID.randomUUID();
    private final LLMProvider llm;

    public DatabaseAgent(LLMProvider llm) {
        this.llm = llm;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String type() {
        return "database";
    }

    @Override
    public AgentResult execute(AgentResult input, AgentContext context) {
        String prompt = "Design database schema from architecture: \n" + input.content();
        Map<String, Object> response = llm.call(prompt, Map.of());
        String text = (String) response.getOrDefault("text", "");
        return new AgentResult(UUID.randomUUID(), "DatabaseSpecification", text);
    }
}