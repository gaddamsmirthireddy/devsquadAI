package com.devsquad.security;

import com.devsquad.project.Artifact;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SecurityEngine {

    public SecurityAssessment analyze(List<Artifact> artifacts) {
        String combined = artifacts.stream().map(Artifact::getContent).reduce("", (a, b) -> a + "\n" + b).toLowerCase(Locale.ROOT);

        int critical = containsAny(combined, "hardcoded-secret", "private_key", "disable-auth", "[insecure]") ? 1 : 0;
        int high = containsAny(combined, "password=", "api_key", "disable-csrf", "eval(") ? 1 : 0;
        int medium = containsAny(combined, "todo security", "allow all", "permitall") ? 1 : 0;
        int low = containsAny(combined, "mock_response") ? 1 : 0;

        boolean passed = critical == 0 && high == 0;
        String summary = passed
                ? "No critical/high security issues detected by deterministic policy checks."
                : "Security gate failed due to critical/high patterns in generated artifacts.";
        return new SecurityAssessment(critical, high, medium, low, passed, summary);
    }

    private boolean containsAny(String content, String... patterns) {
        for (String pattern : patterns) {
            if (content.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
}