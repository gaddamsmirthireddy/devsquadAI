package com.devsquad.config;

import com.devsquad.llm.GeminiProvider;
import com.devsquad.llm.LLMProvider;
import com.devsquad.llm.MockLLMProvider;
import com.devsquad.llm.OpenAIProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
public class AiConfiguration {

    @Bean
    @Profile("!test")
    public LLMProvider llmProvider() {
        return new GeminiProvider();
    }

    @Bean
    @Profile("test")
    public LLMProvider testLlmProvider() {
        return new MockLLMProvider();
    }
}