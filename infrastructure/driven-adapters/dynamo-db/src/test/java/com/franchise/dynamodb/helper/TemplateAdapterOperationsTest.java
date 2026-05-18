package com.franchise.dynamodb.helper;

import com.franchise.dynamodb.franchise.FranchiseEntity;
import com.franchise.model.franchise.Franchise;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.when;

class TemplateAdapterOperationsTest {

    static class TestAdapter extends TemplateAdapterOperations<Franchise, String, FranchiseEntity> {
        public TestAdapter(DynamoDbEnhancedAsyncClient client, ObjectMapper mapper) {
            super(client, mapper, entity -> mapper.map(entity, Franchise.class), "franchises");
        }
    }

    @Mock
    private DynamoDbEnhancedAsyncClient dynamoDbEnhancedAsyncClient;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<FranchiseEntity> table;

    private FranchiseEntity franchiseEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(dynamoDbEnhancedAsyncClient.table("franchises", TableSchema.fromBean(FranchiseEntity.class)))
                .thenReturn(table);
        franchiseEntity = new FranchiseEntity();
        franchiseEntity.setId("id-1");
        franchiseEntity.setName("Test");
    }

    @Test
    void testSave() {
        Franchise franchise = Franchise.builder().id("id-1").name("Test").build();
        when(mapper.map(franchise, FranchiseEntity.class)).thenReturn(franchiseEntity);
        when(table.putItem(franchiseEntity)).thenReturn(CompletableFuture.runAsync(() -> {}));

        TestAdapter adapter = new TestAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(adapter.save(franchise))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void testGetById() {
        Franchise franchise = Franchise.builder().id("id-1").name("Test").build();
        when(table.getItem(Key.builder().partitionValue(
                AttributeValue.builder().s("id-1").build()).build()))
                .thenReturn(CompletableFuture.completedFuture(franchiseEntity));
        when(mapper.map(franchiseEntity, Franchise.class)).thenReturn(franchise);

        TestAdapter adapter = new TestAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(adapter.getById("id-1"))
                .expectNextMatches(f -> "id-1".equals(f.getId()))
                .verifyComplete();
    }

    @Test
    void testDelete() {
        Franchise franchise = Franchise.builder().id("id-1").name("Test").build();
        when(mapper.map(franchise, FranchiseEntity.class)).thenReturn(franchiseEntity);
        when(mapper.map(franchiseEntity, Franchise.class)).thenReturn(franchise);
        when(table.deleteItem(franchiseEntity))
                .thenReturn(CompletableFuture.completedFuture(franchiseEntity));

        TestAdapter adapter = new TestAdapter(dynamoDbEnhancedAsyncClient, mapper);

        StepVerifier.create(adapter.delete(franchise))
                .expectNextMatches(f -> "id-1".equals(f.getId()))
                .verifyComplete();
    }
}
