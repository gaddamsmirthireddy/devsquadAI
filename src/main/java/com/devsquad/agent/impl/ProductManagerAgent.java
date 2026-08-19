package com.devsquad.agent.impl;

import com.devsquad.agent.Agent;
import com.devsquad.agent.AgentContext;
import com.devsquad.agent.AgentResult;
import com.devsquad.llm.LLMProvider;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class ProductManagerAgent implements Agent<String, AgentResult> {
    private final UUID id = UUID.randomUUID();
    private final LLMProvider llm;

    public ProductManagerAgent(LLMProvider llm) {
        this.llm = llm;
    }

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String type() {
        return "product-manager";
    }

    @Override
    public AgentResult execute(String input, AgentContext context) {
        if (input != null && input.contains("[FORCE_FAIL]")) {
            throw new IllegalStateException("Forced failure for retry/approval test path");
        }
        String prompt = "Extract requirements from: " + input;
        Map<String, Object> response = llm.call(prompt, Map.of());
        String text = (String) response.getOrDefault("text", "");
        return new AgentResult(UUID.randomUUID(), "RequirementsSpecification", text);
    }
}