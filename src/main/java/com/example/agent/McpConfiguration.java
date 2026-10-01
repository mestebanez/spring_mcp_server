package com.example.agent;

import io.micrometer.observation.Observations;
import org.springframework.ai.bedrock.titan.BedrockTitanEmbeddingModel;
import org.springframework.ai.bedrock.titan.api.TitanEmbeddingBedrockApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;

@Configuration
public class McpConfiguration {
    @Bean("mymodel")
    public BedrockTitanEmbeddingModel embeddingModel() {
        return new BedrockTitanEmbeddingModel(
        new TitanEmbeddingBedrockApi(
                "amazon.titan-embed-text-v2:0",
                "eu-west-1",
                Duration.ofSeconds(30)), Observations.getGlobalRegistry());
    }
}
