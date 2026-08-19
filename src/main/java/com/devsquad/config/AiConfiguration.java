package com.devsquad.config;

import com.devsquad.llm.LLMProvider;
import com.devsquad.llm.OpenAIProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfiguration {

    @Bean
    public LLMProvider llmProvider() {
        return new OpenAIProvider();
    }
}