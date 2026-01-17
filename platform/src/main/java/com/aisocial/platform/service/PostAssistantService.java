package com.aisocial.platform.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class PostAssistantService {

    private static final Logger log = LoggerFactory.getLogger(PostAssistantService.class);

    private final ChatClient chatClient;

    @Value("${app.fact-check.demo-mode:false}")
    private boolean demoMode;

    public PostAssistantService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Improve existing post content.
     */
    @Retryable(
        retryFor = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public PostAssistantResult improvePost(String content, String instruction) {
        if (content == null || content.trim().isEmpty()) {
            return PostAssistantResult.error("No content provided to improve");
        }

        if (demoMode) {
            return generateDemoImproveResult(content);
        }

        String prompt = buildImprovePrompt(content, instruction);

        try {
            log.info("Sending improve request to AI");

            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            return parseResponse(response);

        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage(), e);
            return PostAssistantResult.error("AI service unavailable: " + e.getMessage());
        }
    }

    /**
     * Generate a new post from a prompt/description.
     */
    @Retryable(
        retryFor = { Exception.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    public PostAssistantResult generatePost(String prompt) {
        if (prompt == null || prompt.trim().isEmpty()) {
            return PostAssistantResult.error("No prompt provided");
        }

        if (demoMode) {
            return generateDemoGenerateResult(prompt);
        }

        String systemPrompt = buildGeneratePrompt(prompt);

        try {
            log.info("Sending generate request to AI");

            String response = chatClient.prompt()
                    .user(systemPrompt)
                    .call()
                    .content();

            return parseResponse(response);

        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage(), e);
            return PostAssistantResult.error("AI service unavailable: " + e.getMessage());
        }
    }

    private String buildImprovePrompt(String content, String instruction) {
        String additionalInstruction = instruction != null && !instruction.isEmpty()
                ? "\n\nUser's specific request: " + instruction
                : "";

        return """
            You are a helpful writing assistant for a social media platform.
            Improve the following post while keeping it under 280 characters.
            %s

            Original post:
            <post>
            %s
            </post>

            Provide 3 different improved versions with varying tones.
            Respond with ONLY valid JSON (no markdown):
            {
              "suggestions": [
                {"text": "<improved version 1>", "tone": "<tone description>"},
                {"text": "<improved version 2>", "tone": "<tone description>"},
                {"text": "<improved version 3>", "tone": "<tone description>"}
              ]
            }

            Keep each suggestion under 280 characters. Make them engaging and shareable.
            """.formatted(additionalInstruction, content);
    }

    private String buildGeneratePrompt(String userPrompt) {
        return """
            You are a helpful writing assistant for a social media platform.
            Generate a post based on the user's description. Keep it under 280 characters.

            User wants to post about:
            <description>
            %s
            </description>

            Provide 3 different post options with varying tones/styles.
            Respond with ONLY valid JSON (no markdown):
            {
              "suggestions": [
                {"text": "<post option 1>", "tone": "<tone description>"},
                {"text": "<post option 2>", "tone": "<tone description>"},
                {"text": "<post option 3>", "tone": "<tone description>"}
              ]
            }

            Keep each suggestion under 280 characters. Make them engaging and shareable.
            """.formatted(userPrompt);
    }

    private PostAssistantResult parseResponse(String response) {
        try {
            // Extract JSON from response
            String json = response.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*$", "");
            }

            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, PostAssistantResult.class);

        } catch (Exception e) {
            log.error("Failed to parse AI response: {}", response, e);
            return PostAssistantResult.error("Failed to parse AI response");
        }
    }

    private PostAssistantResult generateDemoImproveResult(String content) {
        PostAssistantResult result = new PostAssistantResult();
        result.setSuggestions(java.util.List.of(
            new PostAssistantResult.Suggestion(
                "Enhanced: " + content.substring(0, Math.min(content.length(), 200)) + " [Demo Mode]",
                "Professional"
            ),
            new PostAssistantResult.Suggestion(
                content.substring(0, Math.min(content.length(), 200)) + " - what do you think?",
                "Conversational"
            ),
            new PostAssistantResult.Suggestion(
                "Just realized: " + content.substring(0, Math.min(content.length(), 180)) + " Mind = blown.",
                "Casual"
            )
        ));
        return result;
    }

    private PostAssistantResult generateDemoGenerateResult(String prompt) {
        PostAssistantResult result = new PostAssistantResult();
        result.setSuggestions(java.util.List.of(
            new PostAssistantResult.Suggestion(
                "[Demo] Here's a post about: " + prompt.substring(0, Math.min(prompt.length(), 200)),
                "Informative"
            ),
            new PostAssistantResult.Suggestion(
                "[Demo] Thoughts on " + prompt.substring(0, Math.min(prompt.length(), 200)) + "?",
                "Engaging"
            ),
            new PostAssistantResult.Suggestion(
                "[Demo] Let's talk about " + prompt.substring(0, Math.min(prompt.length(), 180)) + "!",
                "Enthusiastic"
            )
        ));
        return result;
    }

    // Inner class for result
    public static class PostAssistantResult {
        private java.util.List<Suggestion> suggestions;
        private String error;

        public PostAssistantResult() {}

        public static PostAssistantResult error(String message) {
            PostAssistantResult result = new PostAssistantResult();
            result.setError(message);
            return result;
        }

        public java.util.List<Suggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(java.util.List<Suggestion> suggestions) { this.suggestions = suggestions; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public static class Suggestion {
            private String text;
            private String tone;

            public Suggestion() {}

            public Suggestion(String text, String tone) {
                this.text = text;
                this.tone = tone;
            }

            public String getText() { return text; }
            public void setText(String text) { this.text = text; }

            public String getTone() { return tone; }
            public void setTone(String tone) { this.tone = tone; }
        }
    }
}
