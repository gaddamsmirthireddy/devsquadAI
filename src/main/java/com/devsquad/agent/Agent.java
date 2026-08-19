package com.devsquad.agent;

import java.util.UUID;

public interface Agent<I, O> {
    UUID id();
    String type();
    O execute(I input, AgentContext context) throws Exception;
}