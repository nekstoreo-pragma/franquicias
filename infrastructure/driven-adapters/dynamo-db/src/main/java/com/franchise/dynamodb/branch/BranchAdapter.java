package com.franchise.dynamodb.branch;

import com.franchise.dynamodb.helper.TemplateAdapterOperations;
import com.franchise.model.branch.Branch;
import com.franchise.model.branch.gateways.BranchRepository;
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
public class BranchAdapter extends TemplateAdapterOperations<Branch, String, BranchEntity>
        implements BranchRepository {

    private final CircuitBreaker circuitBreaker;

    public BranchAdapter(DynamoDbEnhancedAsyncClient client, ObjectMapper mapper, CircuitBreaker dynamoDbCircuitBreaker) {
        super(client, mapper, entity -> mapper.map(entity, Branch.class), "branches", "franchiseId-index");
        this.circuitBreaker = dynamoDbCircuitBreaker;
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return super.save(branch.toBuilder().id(UUID.randomUUID().toString()).build())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Branch> findById(String id) {
        return getById(id)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(franchiseId).build()))
                .build();
        return queryByIndex(query, "franchiseId-index")
                .flatMapMany(Flux::fromIterable)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        return super.save(branch)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
