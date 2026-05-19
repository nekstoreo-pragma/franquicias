package com.franchise.api;

import com.franchise.api.dto.AddBranchRequest;
import com.franchise.api.dto.AddProductRequest;
import com.franchise.api.dto.CreateFranchiseRequest;
import com.franchise.api.dto.UpdateNameRequest;
import com.franchise.api.dto.UpdateStockRequest;
import com.franchise.model.branch.Branch;
import com.franchise.model.franchise.BranchTopProduct;
import com.franchise.model.franchise.Franchise;
import com.franchise.model.product.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/franchises",
            method = {RequestMethod.POST},
            beanClass = FranchiseHandler.class,
            beanMethod = "createFranchise",
            operation = @Operation(
                operationId = "createFranchise",
                summary = "Create a franchise",
                tags = {"Franchises"},
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = CreateFranchiseRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Franchise created",
                        content = @Content(schema = @Schema(implementation = Franchise.class))),
                    @ApiResponse(responseCode = "400", description = "Name is blank"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{franchiseId}/branches",
            method = {RequestMethod.POST},
            beanClass = BranchHandler.class,
            beanMethod = "addBranch",
            operation = @Operation(
                operationId = "addBranch",
                summary = "Add a branch to a franchise",
                tags = {"Branches"},
                parameters = {
                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true,
                        description = "ID of the franchise", example = "f-abc123")
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = AddBranchRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Branch added",
                        content = @Content(schema = @Schema(implementation = Branch.class))),
                    @ApiResponse(responseCode = "400", description = "Name is blank"),
                    @ApiResponse(responseCode = "404", description = "Franchise not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/branches/{branchId}/products",
            method = {RequestMethod.POST},
            beanClass = ProductHandler.class,
            beanMethod = "addProduct",
            operation = @Operation(
                operationId = "addProduct",
                summary = "Add a product to a branch",
                tags = {"Products"},
                parameters = {
                    @Parameter(name = "branchId", in = ParameterIn.PATH, required = true,
                        description = "ID of the branch", example = "b-abc123")
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = AddProductRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Product added",
                        content = @Content(schema = @Schema(implementation = Product.class))),
                    @ApiResponse(responseCode = "400", description = "Name is blank or stock is negative"),
                    @ApiResponse(responseCode = "404", description = "Branch not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/products/{productId}",
            method = {RequestMethod.DELETE},
            beanClass = ProductHandler.class,
            beanMethod = "deleteProduct",
            operation = @Operation(
                operationId = "deleteProduct",
                summary = "Remove a product from a branch",
                tags = {"Products"},
                parameters = {
                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                        description = "ID of the product to delete", example = "p-abc123")
                },
                responses = {
                    @ApiResponse(responseCode = "204", description = "Product deleted"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/products/{productId}/stock",
            method = {RequestMethod.PATCH},
            beanClass = ProductHandler.class,
            beanMethod = "updateProductStock",
            operation = @Operation(
                operationId = "updateProductStock",
                summary = "Update the stock of a product",
                tags = {"Products"},
                parameters = {
                    @Parameter(name = "productId", in = ParameterIn.PATH, required = true,
                        description = "ID of the product", example = "p-abc123")
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UpdateStockRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Stock updated",
                        content = @Content(schema = @Schema(implementation = Product.class))),
                    @ApiResponse(responseCode = "400", description = "Stock is negative"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{franchiseId}/top-products",
            method = {RequestMethod.GET},
            beanClass = FranchiseHandler.class,
            beanMethod = "getTopProductPerBranch",
            operation = @Operation(
                operationId = "getTopProductPerBranch",
                summary = "Get the top-stock product per branch within a franchise",
                tags = {"Franchises"},
                parameters = {
                    @Parameter(name = "franchiseId", in = ParameterIn.PATH, required = true,
                        description = "ID of the franchise", example = "f-abc123")
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "List of branches with their top-stock product",
                        content = @Content(array = @ArraySchema(schema = @Schema(implementation = BranchTopProduct.class)))),
                    @ApiResponse(responseCode = "404", description = "Franchise not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{id}/name",
            method = {RequestMethod.PATCH},
            beanClass = FranchiseHandler.class,
            beanMethod = "updateFranchiseName",
            operation = @Operation(
                operationId = "updateFranchiseName",
                summary = "Update the name of a franchise",
                tags = {"Franchises"},
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                        description = "ID of the franchise", example = "f-abc123")
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UpdateNameRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Franchise name updated",
                        content = @Content(schema = @Schema(implementation = Franchise.class))),
                    @ApiResponse(responseCode = "400", description = "Name is blank"),
                    @ApiResponse(responseCode = "404", description = "Franchise not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/branches/{id}/name",
            method = {RequestMethod.PATCH},
            beanClass = BranchHandler.class,
            beanMethod = "updateBranchName",
            operation = @Operation(
                operationId = "updateBranchName",
                summary = "Update the name of a branch",
                tags = {"Branches"},
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                        description = "ID of the branch", example = "b-abc123")
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UpdateNameRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Branch name updated",
                        content = @Content(schema = @Schema(implementation = Branch.class))),
                    @ApiResponse(responseCode = "400", description = "Name is blank"),
                    @ApiResponse(responseCode = "404", description = "Branch not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/products/{id}/name",
            method = {RequestMethod.PATCH},
            beanClass = ProductHandler.class,
            beanMethod = "updateProductName",
            operation = @Operation(
                operationId = "updateProductName",
                summary = "Update the name of a product",
                tags = {"Products"},
                parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                        description = "ID of the product", example = "p-abc123")
                },
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(
                        mediaType = MediaType.APPLICATION_JSON_VALUE,
                        schema = @Schema(implementation = UpdateNameRequest.class)
                    )
                ),
                responses = {
                    @ApiResponse(responseCode = "200", description = "Product name updated",
                        content = @Content(schema = @Schema(implementation = Product.class))),
                    @ApiResponse(responseCode = "400", description = "Name is blank"),
                    @ApiResponse(responseCode = "404", description = "Product not found"),
                    @ApiResponse(responseCode = "500", description = "Unexpected error")
                }
            )
        )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(FranchiseHandler franchiseHandler,
                                                         BranchHandler branchHandler,
                                                         ProductHandler productHandler) {
        return route(POST("/api/v1/franchises").and(accept(MediaType.APPLICATION_JSON)),
                        franchiseHandler::createFranchise)
                .andRoute(POST("/api/v1/franchises/{franchiseId}/branches").and(accept(MediaType.APPLICATION_JSON)),
                        branchHandler::addBranch)
                .andRoute(POST("/api/v1/branches/{branchId}/products").and(accept(MediaType.APPLICATION_JSON)),
                        productHandler::addProduct)
                .andRoute(DELETE("/api/v1/products/{productId}"),
                        productHandler::deleteProduct)
                .andRoute(PATCH("/api/v1/products/{productId}/stock").and(accept(MediaType.APPLICATION_JSON)),
                        productHandler::updateProductStock)
                .andRoute(GET("/api/v1/franchises/{franchiseId}/top-products"),
                        franchiseHandler::getTopProductPerBranch)
                .andRoute(PATCH("/api/v1/franchises/{id}/name").and(accept(MediaType.APPLICATION_JSON)),
                        franchiseHandler::updateFranchiseName)
                .andRoute(PATCH("/api/v1/branches/{id}/name").and(accept(MediaType.APPLICATION_JSON)),
                        branchHandler::updateBranchName)
                .andRoute(PATCH("/api/v1/products/{id}/name").and(accept(MediaType.APPLICATION_JSON)),
                        productHandler::updateProductName);
    }
}
