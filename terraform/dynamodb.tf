resource "aws_dynamodb_table" "franchises" {
  name         = "franchises"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  tags = {
    Application = var.app_name
  }
}

resource "aws_dynamodb_table" "branches" {
  name         = "branches"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "franchiseId"
    type = "S"
  }

  global_secondary_index {
    name            = "franchiseId-index"
    hash_key        = "franchiseId"
    projection_type = "ALL"
  }

  tags = {
    Application = var.app_name
  }
}

resource "aws_dynamodb_table" "products" {
  name         = "products"
  billing_mode = "PAY_PER_REQUEST"
  hash_key     = "id"

  attribute {
    name = "id"
    type = "S"
  }

  attribute {
    name = "branchId"
    type = "S"
  }

  global_secondary_index {
    name            = "branchId-index"
    hash_key        = "branchId"
    projection_type = "ALL"
  }

  tags = {
    Application = var.app_name
  }
}
