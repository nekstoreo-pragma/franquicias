package com.franchise.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to add a product to a branch")
public record AddProductRequest(
        @Schema(description = "Product name", example = "Whopper", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Initial stock (must be >= 0)", example = "50", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int stock
) {}
