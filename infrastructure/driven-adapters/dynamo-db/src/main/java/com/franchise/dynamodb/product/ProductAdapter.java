package com.franchise.dynamodb.product;

import com.franchise.dynamodb.helper.TemplateAdapterOperations;
import com.franchise.model.product.Product;
import com.franchise.model.product.gateways.ProductRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.UUID;

@Repository
public class ProductAdapter extends TemplateAdapterOperations<Product, String, ProductEntity>
        implements ProductRepository {

    private final CircuitBreaker circuitBreaker;

    public ProductAdapter(DynamoDbEnhancedAsyncClient client, ObjectMapper mapper, CircuitBreaker dynamoDbCircuitBreaker) {
        super(client, mapper, entity -> mapper.map(entity, Product.class), "products", "branchId-index");
        this.circuitBreaker = dynamoDbCircuitBreaker;
    }

    @Override
    public Mono<Product> save(Product product) {
        return super.save(product.toBuilder().id(UUID.randomUUID().toString()).build())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Product> findById(String id) {
        return getById(id)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Flux<Product> findByBranchId(String branchId) {
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(branchId).build()))
                .build();
        return queryByIndex(query, "branchId-index")
                .flatMapMany(Flux::fromIterable)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Product> update(Product product) {
        return super.save(product)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Void> delete(String id) {
        return super.delete(Product.builder().id(id).build()).then()
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
