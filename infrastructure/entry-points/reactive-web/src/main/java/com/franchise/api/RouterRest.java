package com.franchise.api;

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
            beanMethod = "createFranchise"
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{franchiseId}/branches",
            method = {RequestMethod.POST},
            beanClass = BranchHandler.class,
            beanMethod = "addBranch"
        ),
        @RouterOperation(
            path = "/api/v1/branches/{branchId}/products",
            method = {RequestMethod.POST},
            beanClass = ProductHandler.class,
            beanMethod = "addProduct"
        ),
        @RouterOperation(
            path = "/api/v1/products/{productId}",
            method = {RequestMethod.DELETE},
            beanClass = ProductHandler.class,
            beanMethod = "deleteProduct"
        ),
        @RouterOperation(
            path = "/api/v1/products/{productId}/stock",
            method = {RequestMethod.PATCH},
            beanClass = ProductHandler.class,
            beanMethod = "updateProductStock"
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{franchiseId}/top-products",
            method = {RequestMethod.GET},
            beanClass = FranchiseHandler.class,
            beanMethod = "getTopProductPerBranch"
        ),
        @RouterOperation(
            path = "/api/v1/franchises/{id}/name",
            method = {RequestMethod.PATCH},
            beanClass = FranchiseHandler.class,
            beanMethod = "updateFranchiseName"
        ),
        @RouterOperation(
            path = "/api/v1/branches/{id}/name",
            method = {RequestMethod.PATCH},
            beanClass = BranchHandler.class,
            beanMethod = "updateBranchName"
        ),
        @RouterOperation(
            path = "/api/v1/products/{id}/name",
            method = {RequestMethod.PATCH},
            beanClass = ProductHandler.class,
            beanMethod = "updateProductName"
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
