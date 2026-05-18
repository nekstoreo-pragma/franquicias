package com.franchise.dynamodb.branch;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932 */
@DynamoDbBean
public class BranchEntity {

    private String id;
    private String name;
    private String franchiseId;

    public BranchEntity() {}

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @DynamoDbAttribute("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @DynamoDbSecondaryPartitionKey(indexNames = "franchiseId-index")
    @DynamoDbAttribute("franchiseId")
    public String getFranchiseId() { return franchiseId; }
    public void setFranchiseId(String franchiseId) { this.franchiseId = franchiseId; }
}
