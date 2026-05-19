variable "aws_region" {
  description = "AWS region where all resources will be created"
  type        = string
  default     = "us-east-1"
}

variable "app_name" {
  description = "Application name, used as prefix for all resource names"
  type        = string
  default     = "franchise-api"
}

variable "image_tag" {
  description = "Docker image tag to deploy (ECR image tag)"
  type        = string
  default     = "latest"
}

variable "spring_profile" {
  description = "Spring Boot active profile (dev | cer | pdn)"
  type        = string
  default     = "dev"
}

variable "task_cpu" {
  description = "ECS task CPU units (1024 = 1 vCPU)"
  type        = number
  default     = 512
}

variable "task_memory" {
  description = "ECS task memory in MiB"
  type        = number
  default     = 1024
}

variable "desired_count" {
  description = "Number of ECS task instances to run"
  type        = number
  default     = 1
}
