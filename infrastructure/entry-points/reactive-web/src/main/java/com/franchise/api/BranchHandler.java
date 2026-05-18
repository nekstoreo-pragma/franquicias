package com.franchise.api;

import com.franchise.api.dto.AddBranchRequest;
import com.franchise.model.branch.Branch;
import com.franchise.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchUseCase branchUseCase;

    public Mono<ServerResponse> addBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(AddBranchRequest.class)
                .flatMap(req -> {
                    if (req.name() == null || req.name().isBlank()) {
                        return ServerResponse.badRequest()
                                .bodyValue("Branch name is required");
                    }
                    return branchUseCase.addBranch(franchiseId, Branch.builder().name(req.name()).build())
                            .flatMap(created -> ServerResponse.status(HttpStatus.CREATED).bodyValue(created));
                })
                .onErrorResume(NoSuchElementException.class, e ->
                        ServerResponse.status(HttpStatus.NOT_FOUND).bodyValue("Franchise not found"))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .bodyValue("An error occurred"));
    }
}
