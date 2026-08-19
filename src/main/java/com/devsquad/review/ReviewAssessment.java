package com.devsquad.review;

public record ReviewAssessment(int criticalCount,
                               int highCount,
                               int mediumCount,
                               int lowCount,
                               boolean passed,
                               String summary) {
}