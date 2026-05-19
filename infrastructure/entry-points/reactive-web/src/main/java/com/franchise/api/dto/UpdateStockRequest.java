package com.franchise.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to update a product's stock")
public record UpdateStockRequest(
        @Schema(description = "New stock value (must be >= 0)", example = "99", minimum = "0", requiredMode = Schema.RequiredMode.REQUIRED)
        int stock
) {}
