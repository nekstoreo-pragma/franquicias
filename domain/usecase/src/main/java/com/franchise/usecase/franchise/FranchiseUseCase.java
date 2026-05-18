package com.franchise.usecase.franchise;

import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> create(Franchise franchise) {
        return franchiseRepository.save(franchise);
    }
}
