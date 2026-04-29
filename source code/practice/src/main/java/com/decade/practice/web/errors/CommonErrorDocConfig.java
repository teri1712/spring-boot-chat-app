package com.decade.practice.web.errors;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommonErrorDocConfig {
    @Bean
    OpenApiCustomizer validationErrorCustomizer() {
        return new OpenApiCustomizer() {
            @Override
            public void customise(OpenAPI openApi) {
                openApi.getComponents().addExamples("Validation",
                    new Example()
                        .summary("Validation failure")
                        .value(Map.of(
                            "type", "https://example.com/errors/validation",
                            "title", "Validation failure",
                            "status", 400,
                            "detail", "Email is invalid"
                        ))
                );

            }
        };
    }

    @Bean
    OpenApiCustomizer notfoundErrorCustomizer() {
        return new OpenApiCustomizer() {
            @Override
            public void customise(OpenAPI openApi) {
                openApi.getComponents().addExamples("NotFound",
                    new Example()
                        .summary("Resource not found")
                        .value(Map.of(
                            "title", "The requested resource was not found",
                            "status", 404
                        ))
                );

            }
        };
    }
}
