package com.devsquad.llm;

import java.util.HashMap;
import java.util.Map;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

public class OpenAIProvider implements LLMProvider {

    private final OpenAIClient client;

    public OpenAIProvider() {
        this.client = OpenAIOkHttpClient.fromEnv();
    }

    @Override
    public String name() {
        return "openai";
    }

    @Override
    public Map<String, Object> call(String prompt, Map<String, Object> options) {

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model(ChatModel.GPT_5_2)
                .build();

        Response response = client.responses().create(params);

        String text = response.output()
                .stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElse("");

        Map<String, Object> result = new HashMap<>();
        result.put("text", text);

        return result;
    }
}