package com.franchise.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request body to update the name of a resource")
public record UpdateNameRequest(
        @Schema(description = "New name (cannot be blank)", example = "Sede Norte Actualizada", requiredMode = Schema.RequiredMode.REQUIRED)
        String name
) {}
