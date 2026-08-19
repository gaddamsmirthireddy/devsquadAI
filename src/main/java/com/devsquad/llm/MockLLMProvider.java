package com.devsquad.llm;

import java.util.HashMap;
import java.util.Map;

public class MockLLMProvider implements LLMProvider {

    @Override
    public String name() {
        return "mock";
    }

    @Override
    public Map<String, Object> call(String prompt, Map<String, Object> options) {
        Map<String, Object> response = new HashMap<>();
        response.put("text", "MOCK_RESPONSE: " +
                prompt.substring(0, Math.min(120, prompt.length())));
        return response;
    }
}
