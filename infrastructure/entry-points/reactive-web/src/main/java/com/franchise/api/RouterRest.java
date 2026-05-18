package com.franchise.api;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

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
                        productHandler::addProduct);
    }
}
