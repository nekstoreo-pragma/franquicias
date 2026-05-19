package com.franchise.api;

import com.franchise.api.dto.CreateFranchiseRequest;
import com.franchise.api.dto.UpdateNameRequest;
import com.franchise.model.franchise.Franchise;
import com.franchise.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

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

    public Mono<ServerResponse> getTopProductPerBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return franchiseUseCase.getTopProductPerBranch(franchiseId)
                .flatMap(list -> ServerResponse.ok().bodyValue(list))
                .onErrorResume(NoSuchElementException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Franchise not found"))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("An error occurred"));
    }

    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(UpdateNameRequest.class)
                .flatMap(req -> franchiseUseCase.updateName(id, req.name())
                        .flatMap(updated -> ServerResponse.ok().bodyValue(updated)))
                .onErrorResume(IllegalArgumentException.class, e ->
                        ServerResponse.badRequest().bodyValue(e.getMessage()))
                .onErrorResume(NoSuchElementException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Franchise not found"))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("An error occurred"));
    }
}
