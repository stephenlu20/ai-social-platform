package com.aisocial.platform.controller;

import com.aisocial.platform.service.PostAssistantService;
import com.aisocial.platform.service.PostAssistantService.PostAssistantResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai/post-assistant")
@CrossOrigin(origins = "*")
public class PostAssistantController {

    private final PostAssistantService postAssistantService;

    public PostAssistantController(PostAssistantService postAssistantService) {
        this.postAssistantService = postAssistantService;
    }

    /**
     * Improve existing post content
     */
    @PostMapping("/improve")
    public ResponseEntity<PostAssistantResult> improvePost(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        String instruction = request.get("instruction"); // Optional specific instruction

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(PostAssistantResult.error("Content is required"));
        }

        PostAssistantResult result = postAssistantService.improvePost(content, instruction);
        return ResponseEntity.ok(result);
    }

    /**
     * Generate a new post from a prompt
     */
    @PostMapping("/generate")
    public ResponseEntity<PostAssistantResult> generatePost(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");

        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(PostAssistantResult.error("Prompt is required"));
        }

        PostAssistantResult result = postAssistantService.generatePost(prompt);
        return ResponseEntity.ok(result);
    }
}
