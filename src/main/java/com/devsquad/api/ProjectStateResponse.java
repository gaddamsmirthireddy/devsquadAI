package com.devsquad.api;

import java.util.UUID;

public record ProjectStateResponse(UUID projectId,
                                   String status,
                                   String currentPhase,
                                   boolean approvalRequired) {
}