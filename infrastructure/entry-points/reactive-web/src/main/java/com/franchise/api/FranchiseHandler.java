package com.franchise.api;

import com.franchise.api.dto.CreateFranchiseRequest;
import com.franchise.model.franchise.Franchise;
import com.franchise.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .flatMap(req -> {
                    if (req.name() == null || req.name().isBlank()) {
                        return ServerResponse.badRequest()
                                .bodyValue("Franchise name is required");
                    }
                    return franchiseUseCase.create(Franchise.builder().name(req.name()).build())
                            .flatMap(created -> ServerResponse.status(HttpStatus.CREATED).bodyValue(created));
                })
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("An error occurred"));
    }
}
