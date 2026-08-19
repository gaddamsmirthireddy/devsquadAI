package com.devsquad.orchestration;

import com.devsquad.agent.Agent;
import com.devsquad.agent.AgentResult;
import com.devsquad.agent.impl.ApiDesignAgent;
import com.devsquad.agent.impl.ArchitectAgent;
import com.devsquad.agent.impl.DatabaseAgent;
import com.devsquad.agent.impl.ProductManagerAgent;
import com.devsquad.project.AgentType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class AgentRegistry {
    private final Map<AgentType, Agent<?, AgentResult>> agents = new EnumMap<>(AgentType.class);

    public AgentRegistry(ProductManagerAgent productManagerAgent,
                         ArchitectAgent architectAgent,
                         DatabaseAgent databaseAgent,
                         ApiDesignAgent apiDesignAgent) {
        agents.put(AgentType.PRODUCT_MANAGER, productManagerAgent);
        agents.put(AgentType.ARCHITECT, architectAgent);
        agents.put(AgentType.DATABASE, databaseAgent);
        agents.put(AgentType.API_DESIGN, apiDesignAgent);
    }

    public Agent<?, AgentResult> get(AgentType agentType) {
        Agent<?, AgentResult> agent = agents.get(agentType);
        if (agent == null) {
            throw new IllegalArgumentException("No agent registered for type " + agentType);
        }
        return agent;
    }
}