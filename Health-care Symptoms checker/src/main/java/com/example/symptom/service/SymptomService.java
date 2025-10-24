package com.example.symptom.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.symptom.dto.SymptomRequest;
import com.example.symptom.dto.SymptomResponse;
import com.example.symptom.model.OpenAiClient;
import com.example.symptom.model.SymptomHistory;
import com.example.symptom.repository.SymptomHistoryRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class SymptomService {

    private final OpenAiClient openAiClient;
    private final SymptomHistoryRepository historyRepository;

    public SymptomService(OpenAiClient openAiClient, SymptomHistoryRepository historyRepository) {
        this.openAiClient = openAiClient;
        this.historyRepository = historyRepository;
    }

    private String buildSystemPrompt() {
        return "You are a medical-knowledge assistant. Provide educational information only. "
             + "Always include a clear disclaimer that you are not a doctor and recommend seeing a licensed physician "
             + "for diagnosis. Use concise bullet lists for probable conditions and next steps. "
             + "If symptoms are severe or life-threatening, instruct user to seek emergency care immediately.";
    }

    private String buildUserPrompt(SymptomRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient description: ").append(req.getSymptoms()).append("\n");
        if (req.getAge() != null) sb.append("Age: ").append(req.getAge()).append("\n");
        if (req.getSex() != null) sb.append("Sex: ").append(req.getSex()).append("\n");
        if (req.getExistingConditions() != null) sb.append("Existing conditions: ").append(req.getExistingConditions()).append("\n");
        sb.append("\nRespond with:\n1) A short disclaimer sentence.\n2) A list (3 max) of possible conditions (each 3–8 words).\n3) Recommended next steps (3–6 actionable items).");
        return sb.toString();
    }

    @Transactional
    public SymptomResponse analyze(SymptomRequest req, Long userId) {
        String system = buildSystemPrompt();
        String userPrompt = buildUserPrompt(req);

        String raw = null;
        try {
            //  Try calling OpenAI — catch failures like "quota exceeded"
            raw = openAiClient.createChatCompletion(system, userPrompt).block();
        } catch (Exception e) {
            //  Graceful fallback response
            raw = "⚠️ Unable to fetch AI response. Reason: " + e.getMessage()
                + "\nThis may happen if your API key is invalid or quota is exceeded.";
        }

        SymptomResponse out = new SymptomResponse();
        out.setRawModelText(raw);

        // ✅ Prevent NPEs on parsing if raw is null or empty
        if (raw == null || raw.isBlank()) {
            out.setDisclaimer("Service temporarily unavailable. Please try again later.");
            out.setProbableConditions(List.of());
            out.setRecommendedNextSteps(List.of());
            return out;
        }

        String[] parts = raw.split("\\n\\n");
        out.setDisclaimer(parts.length > 0 ? parts[0].trim() : "Educational only.");
        List<String> conditions = new ArrayList<>();
        List<String> steps = new ArrayList<>();

        for (String p : parts) {
            String lower = p.toLowerCase();
            if (lower.contains("possible") || lower.contains("conditions") || lower.contains("diagnosis")) {
                for (String line : p.split("\\n")) {
                    line = line.trim();
                    if (line.startsWith("-") || line.startsWith("•") || line.matches("^\\d+\\.")) {
                        conditions.add(line.replaceAll("^[-•\\d.\\s]+", ""));
                    }
                }
            } else if (lower.contains("next") || lower.contains("recommend")) {
                for (String line : p.split("\\n")) {
                    line = line.trim();
                    if (line.startsWith("-") || line.startsWith("•") || line.matches("^\\d+\\.")) {
                        steps.add(line.replaceAll("^[-•\\d.\\s]+", ""));
                    }
                }
            }
        }
        if (conditions.isEmpty()) {
            for (String line : raw.split("\\n")) {
                if (line.startsWith("-") && conditions.size() < 3)
                    conditions.add(line.replaceFirst("^[-•\\s]+", ""));
            }
        }

        out.setProbableConditions(conditions);
        out.setRecommendedNextSteps(steps);

        // save history (blocking JPA)
        try {
            SymptomHistory h = new SymptomHistory();
            h.setUserId(userId);
            h.setRequestText(userPrompt);
            h.setResponseText(raw);
            historyRepository.save(h);
        } catch (Exception e) {
            System.err.println("⚠️ Failed to save history: " + e.getMessage());
        }

        return out;
    }

    // simple history fetch
    public List<SymptomHistory> getHistoryForUser(Long userId) {
        if (userId == null) return List.of();
        return historyRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
