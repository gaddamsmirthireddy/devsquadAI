package com.devsquad.review;

import com.devsquad.project.Artifact;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class ReviewEngine {

    public ReviewAssessment review(List<Artifact> artifacts) {
        String combined = artifacts.stream().map(Artifact::getContent).reduce("", (a, b) -> a + "\n" + b).toLowerCase(Locale.ROOT);

        int critical = containsAny(combined, "compile error", "nullpointerexception") ? 1 : 0;
        int high = containsAny(combined, "god class", "duplicate logic") ? 1 : 0;
        int medium = containsAny(combined, "todo", "fixme") ? 1 : 0;
        int low = containsAny(combined, "mock_response") ? 1 : 0;

        boolean passed = critical == 0 && high == 0;
        String summary = passed
                ? "Code review gate passed deterministic quality checks."
                : "Code review gate failed due to high severity maintainability/correctness indicators.";
        return new ReviewAssessment(critical, high, medium, low, passed, summary);
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