package com.franchise.usecase.branch;

import com.franchise.model.branch.Branch;
import com.franchise.model.branch.gateways.BranchRepository;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class BranchUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public Mono<Branch> addBranch(String franchiseId, Branch branch) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Franchise not found: " + franchiseId)))
                .flatMap(franchise -> branchRepository.save(
                        branch.toBuilder().franchiseId(franchiseId).build()));
    }
}
