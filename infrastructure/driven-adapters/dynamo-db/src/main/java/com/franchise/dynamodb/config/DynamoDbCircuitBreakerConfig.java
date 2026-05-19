package com.franchise.dynamodb.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DynamoDbCircuitBreakerConfig {

    @Bean
    public CircuitBreaker dynamoDbCircuitBreaker() {
        var config = io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofSeconds(30))
                .permittedNumberOfCallsInHalfOpenState(3)
                .build();

        return CircuitBreaker.of("dynamodb", config);
    }
}
