#!/usr/bin/env bash
set -e

ENDPOINT="http://localhost:8000"
REGION="us-east-1"

aws dynamodb create-table \
  --table-name franchises \
  --attribute-definitions AttributeName=id,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT" \
  --region "$REGION"

aws dynamodb create-table \
  --table-name branches \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=franchiseId,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --global-secondary-indexes '[{
    "IndexName":"franchiseId-index",
    "KeySchema":[{"AttributeName":"franchiseId","KeyType":"HASH"}],
    "Projection":{"ProjectionType":"ALL"}
  }]' \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT" \
  --region "$REGION"

aws dynamodb create-table \
  --table-name products \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=branchId,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --global-secondary-indexes '[{
    "IndexName":"branchId-index",
    "KeySchema":[{"AttributeName":"branchId","KeyType":"HASH"}],
    "Projection":{"ProjectionType":"ALL"}
  }]' \
  --billing-mode PAY_PER_REQUEST \
  --endpoint-url "$ENDPOINT" \
  --region "$REGION"

echo "Tables created successfully."
