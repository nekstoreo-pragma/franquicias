package com.franchise.dynamodb.franchise;

import com.franchise.dynamodb.helper.TemplateAdapterOperations;
import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;

import java.util.UUID;

@Repository
public class FranchiseAdapter extends TemplateAdapterOperations<Franchise, String, FranchiseEntity>
        implements FranchiseRepository {

    private final CircuitBreaker circuitBreaker;

    public FranchiseAdapter(DynamoDbEnhancedAsyncClient client, ObjectMapper mapper, CircuitBreaker dynamoDbCircuitBreaker) {
        super(client, mapper, entity -> mapper.map(entity, Franchise.class), "franchises");
        this.circuitBreaker = dynamoDbCircuitBreaker;
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return super.save(franchise.toBuilder().id(UUID.randomUUID().toString()).build())
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Franchise> findById(String id) {
        return getById(id)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }

    @Override
    public Mono<Franchise> update(Franchise franchise) {
        return super.save(franchise)
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
    }
}
