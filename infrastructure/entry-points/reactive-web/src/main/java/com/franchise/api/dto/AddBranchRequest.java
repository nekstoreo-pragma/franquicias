package com.franchise.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to add a branch to a franchise")
public record AddBranchRequest(
        @Schema(description = "Branch name", example = "Sede Norte", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {}
