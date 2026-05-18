package com.franchise.dynamodb.product;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

/* Enhanced DynamoDB annotations are incompatible with Lombok #1932 */
@DynamoDbBean
public class ProductEntity {

    private String id;
    private String name;
    private int stock;
    private String branchId;

    public ProductEntity() {}

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @DynamoDbAttribute("name")
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @DynamoDbAttribute("stock")
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    @DynamoDbSecondaryPartitionKey(indexNames = "branchId-index")
    @DynamoDbAttribute("branchId")
    public String getBranchId() { return branchId; }
    public void setBranchId(String branchId) { this.branchId = branchId; }
}
