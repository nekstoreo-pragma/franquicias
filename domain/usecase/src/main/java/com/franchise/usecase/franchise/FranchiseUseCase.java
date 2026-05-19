package com.franchise.usecase.franchise;

import com.franchise.model.branch.gateways.BranchRepository;
import com.franchise.model.franchise.BranchTopProduct;
import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import com.franchise.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Mono<Franchise> create(Franchise franchise) {
        return franchiseRepository.save(franchise);
    }

    public Mono<List<BranchTopProduct>> getTopProductPerBranch(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Franchise not found: " + franchiseId)))
                .flatMapMany(franchise -> branchRepository.findByFranchiseId(franchiseId))
                .flatMap(branch ->
                        productRepository.findByBranchId(branch.getId())
                                .reduce((p1, p2) -> p1.getStock() >= p2.getStock() ? p1 : p2)
                                .map(topProduct -> BranchTopProduct.builder()
                                        .branch(branch)
                                        .topProduct(topProduct)
                                        .build())
                )
                .collectList();
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
