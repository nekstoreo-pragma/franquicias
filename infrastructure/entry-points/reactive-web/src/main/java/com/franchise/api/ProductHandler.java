package com.franchise.api;

import com.franchise.api.dto.AddProductRequest;
import com.franchise.model.product.Product;
import com.franchise.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;

    public Mono<ServerResponse> addProduct(ServerRequest request) {
        String branchId = request.pathVariable("branchId");
        return request.bodyToMono(AddProductRequest.class)
                .flatMap(req -> {
                    if (req.name() == null || req.name().isBlank()) {
                        return ServerResponse.badRequest()
                                .bodyValue("Product name is required");
                    }
                    if (req.stock() < 0) {
                        return ServerResponse.badRequest()
                                .bodyValue("Stock cannot be negative");
                    }
                    return productUseCase.addProduct(branchId,
                                    Product.builder().name(req.name()).stock(req.stock()).build())
                            .flatMap(created -> ServerResponse.status(HttpStatus.CREATED).bodyValue(created));
                })
                .onErrorResume(NoSuchElementException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Branch not found"))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("An error occurred"));
    }
}
