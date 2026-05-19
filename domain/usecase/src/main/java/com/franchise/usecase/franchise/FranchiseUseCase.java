package com.franchise.usecase.franchise;

import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> create(Franchise franchise) {
        return franchiseRepository.save(franchise);
    }

    public Mono<Franchise> updateName(String id, String name) {
        if (name == null || name.isBlank()) {
            return Mono.error(new IllegalArgumentException("Franchise name cannot be blank"));
        }
        return franchiseRepository.findById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Franchise not found: " + id)))
                .flatMap(franchise -> franchiseRepository.update(franchise.toBuilder().name(name).build()));
    }
}
