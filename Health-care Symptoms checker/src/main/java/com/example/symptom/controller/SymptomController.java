package com.example.symptom.controller;

import com.example.symptom.dto.SymptomRequest;
import com.example.symptom.dto.SymptomResponse;
import com.example.symptom.model.SymptomHistory;
import com.example.symptom.service.SymptomService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller that accepts a SymptomRequest and returns SymptomResponse.
 * It extracts userId from the authentication principal if possible.
 */
@RestController
@RequestMapping("/api/symptoms")
public class SymptomController {

    private final SymptomService symptomService;

    public SymptomController(SymptomService symptomService) {
        this.symptomService = symptomService;
    }

    /**
     * Analyze symptoms. Principal may be Jwt, a custom UserDetails, or null (anonymous).
     *
     * This method attempts to extract a numeric userId in this order:
     * 1) If principal is Jwt: claim "id" (as string) or subject
     * 2) If principal is a custom UserDetails with getId(): cast and call getId()
     * 3) If principal is UserDetails: try to parse username to long (if username holds id)
     *
     * Adjust to your authentication strategy.
     */
    @PostMapping
    public ResponseEntity<SymptomResponse> analyze(
            @Valid @RequestBody SymptomRequest req,
            @AuthenticationPrincipal Object principal
    ) {
        Long userId = extractUserId(principal);
        SymptomResponse resp = symptomService.analyze(req, userId);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/history")
    public ResponseEntity<List<SymptomHistory>> history(@AuthenticationPrincipal Object principal) {
        Long userId = extractUserId(principal);
        if (userId == null) return ResponseEntity.status(401).build();
        return ResponseEntity.ok(symptomService.getHistoryForUser(userId));
    }

    /**
     * Centralized extraction helper — extend for your custom principal types.
     */
    private Long extractUserId(Object principal) {
        if (principal == null) return null;

        // Case 1: Jwt (if you use Spring Security JWT support)
        if (principal instanceof Jwt jwt) {
            // Prefer an 'id' claim, else fallback to subject
            String idClaim = null;
            try {
                idClaim = jwt.getClaimAsString("id");
            } catch (Exception ignored) { }
            if (idClaim == null) idClaim = jwt.getSubject(); // subject often contains a unique id
            if (idClaim != null) {
                try {
                    return Long.valueOf(idClaim);
                } catch (NumberFormatException ignored) {
                    // not numeric — could be UUID or email; return null in that case
                    return null;
                }
            }
            return null;
        }

        // Case 2: Custom UserDetails that exposes getId()
        if (principal instanceof UserDetails ud) {
            // If you have a CustomUserDetails with getId(), cast and use it:
            try {
                // Replace CustomUserDetails with your actual class name if present
                Class<?> cls = ud.getClass();
                try {
                    var method = cls.getMethod("getId");
                    Object idObj = method.invoke(ud);
                    if (idObj instanceof Number) return ((Number) idObj).longValue();
                    if (idObj instanceof String) {
                        try { return Long.valueOf((String) idObj); } catch (NumberFormatException ignored) {}
                    }
                } catch (NoSuchMethodException ignored) {
                    // no getId() — fallback to parsing username
                }
            } catch (Exception e) {
                // ignore reflection errors and try fallback
            }

            // Fallback: if username is numeric, parse it (some apps put id in username)
            String username = ud.getUsername();
            if (username != null) {
                try {
                    return Long.valueOf(username);
                } catch (NumberFormatException ignored) {
                    // not numeric, can't extract id
                }
            }
        }

        // Unknown principal type — cannot extract numeric id
        return null;
    }
}
