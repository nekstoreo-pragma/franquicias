package com.franchise.model.product.gateways;

import com.franchise.model.product.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Flux<Product> findByBranchId(String branchId);
    Mono<Product> update(Product product);
    Mono<Void> delete(String id);
}
