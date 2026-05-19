package com.franchise.usecase.product;

import com.franchise.model.branch.gateways.BranchRepository;
import com.franchise.model.product.Product;
import com.franchise.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class ProductUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Mono<Void> removeProduct(String productId) {
        return productRepository.delete(productId);
    }

    public Mono<Product> addProduct(String branchId, Product product) {
        if (product.getStock() < 0) {
            return Mono.error(new IllegalArgumentException("Stock cannot be negative"));
        }
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Branch not found: " + branchId)))
                .flatMap(branch -> productRepository.save(
                        product.toBuilder().branchId(branchId).build()));
    }
}
