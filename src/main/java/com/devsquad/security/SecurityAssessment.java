package com.devsquad.security;

public record SecurityAssessment(int criticalCount,
                                 int highCount,
                                 int mediumCount,
                                 int lowCount,
                                 boolean passed,
                                 String summary) {
}