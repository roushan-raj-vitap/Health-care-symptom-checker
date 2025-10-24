package com.example.symptom.model;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

@Component
public class OpenAiClient {

    private final WebClient webClient;
    private final String model;
    private final Duration timeout;

    public OpenAiClient(
            @Value("${openai.api.base-url:https://api.openai.com/v1}") String baseUrl,
            @Value("${openai.api.key}") String apiKey,
            @Value("${openai.api.model:gpt-4o-mini}") String model,
            @Value("${openai.api.timeout-seconds:30}") long timeoutSeconds
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.model = model;
        this.timeout = Duration.ofSeconds(timeoutSeconds);
    }

    /**
     * Calls the Chat Completions endpoint and returns the text content of the first choice.
     * Returns a Mono<String> — caller can block() if they want a blocking call.
     */
    public Mono<String> createChatCompletion(String systemPrompt, String userPrompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", new Object[]{
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                },
                "temperature", 0.2
        );

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(body)
                .retrieve()
                .onStatus(status -> !status.is2xxSuccessful(), (ClientResponse resp) ->
                        resp.bodyToMono(String.class).defaultIfEmpty("No body")
                            .flatMap(bodyText -> Mono.error(new RuntimeException("OpenAI non-2xx: " + resp.statusCode() + " body: " + bodyText)))
                )
                .bodyToMono(Map.class)
                .timeout(timeout)
                .map(resp -> {
                    // defensive parsing: choices -> [0] -> message -> content
                    Object choicesObj = resp.get("choices");
                    if (choicesObj instanceof java.util.List<?> choices && !choices.isEmpty()) {
                        Object first = choices.get(0);
                        if (first instanceof Map<?, ?> firstMap) {
                            Object messageObj = firstMap.get("message");
                            if (messageObj instanceof Map<?, ?> messageMap) {
                                Object content = messageMap.get("content");
                                if (content != null) {
                                    return content.toString().trim();
                                }
                            }
                            // older responses sometimes put text under "text"
                            Object text = firstMap.get("text");
                            if (text != null) return text.toString().trim();
                        }
                    }
                    // fallback: try top-level "text" or return full map
                    Object textTop = resp.get("text");
                    return textTop != null ? textTop.toString() : resp.toString();
                })
                .onErrorResume(ex -> {
                    // in production, log the exception (SLF4J). Return a friendly error string here.
                    String msg = "Error calling OpenAI: " + ex.getMessage();
                    return Mono.just(msg);
                });
    }
}
