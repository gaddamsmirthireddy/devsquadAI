package com.devsquad.llm;

import java.util.Map;

public interface LLMProvider {
    String name();
    Map<String, Object> call(String prompt, Map<String, Object> options);
}