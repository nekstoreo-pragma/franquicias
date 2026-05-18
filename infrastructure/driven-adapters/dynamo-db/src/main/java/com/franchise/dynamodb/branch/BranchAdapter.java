package com.franchise.dynamodb.branch;

import com.franchise.dynamodb.helper.TemplateAdapterOperations;
import com.franchise.model.branch.Branch;
import com.franchise.model.branch.gateways.BranchRepository;
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

    public BranchAdapter(DynamoDbEnhancedAsyncClient client, ObjectMapper mapper) {
        super(client, mapper, entity -> mapper.map(entity, Branch.class), "branches", "franchiseId-index");
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return super.save(branch.toBuilder().id(UUID.randomUUID().toString()).build());
    }

    @Override
    public Mono<Branch> findById(String id) {
        return getById(id);
    }

    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        QueryEnhancedRequest query = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(
                        Key.builder().partitionValue(franchiseId).build()))
                .build();
        return queryByIndex(query, "franchiseId-index")
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Branch> update(Branch branch) {
        return super.save(branch);
    }
}
