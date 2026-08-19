package com.devsquad.llm;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.util.HashMap;
import java.util.Map;

public class GeminiProvider implements LLMProvider {

    private final Client client;

    public GeminiProvider() {
        this.client = new Client();
    }

    @Override
    public String name() {
        return "gemini";
    }

    @Override
    public Map<String, Object> call(String prompt, Map<String, Object> options) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3.6-flash",
                        prompt,
                        null
                );

        Map<String, Object> result = new HashMap<>();
        result.put("text", response.text());

        return result;
    }
}