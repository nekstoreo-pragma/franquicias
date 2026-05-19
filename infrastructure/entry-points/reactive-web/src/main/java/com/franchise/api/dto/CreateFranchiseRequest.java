package com.franchise.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to create a new franchise")
public record CreateFranchiseRequest(
        @Schema(description = "Franchise name", example = "Burger King", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {}
