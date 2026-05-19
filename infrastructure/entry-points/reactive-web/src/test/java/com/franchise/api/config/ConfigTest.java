package com.franchise.api.config;

import com.franchise.api.BranchHandler;
import com.franchise.api.FranchiseHandler;
import com.franchise.api.ProductHandler;
import com.franchise.api.RouterRest;
import com.franchise.api.dto.CreateFranchiseRequest;
import com.franchise.model.franchise.Franchise;
import com.franchise.usecase.branch.BranchUseCase;
import com.franchise.usecase.franchise.FranchiseUseCase;
import com.franchise.usecase.product.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class, BranchHandler.class, ProductHandler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    @MockitoBean
    private BranchUseCase branchUseCase;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    void securityHeadersShouldBePresentOnAllResponses() {
        when(franchiseUseCase.create(any())).thenReturn(
                Mono.just(Franchise.builder().id("id-1").name("Test").build()));

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranchiseRequest("Test"))
                .exchange()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000; includeSubDomains; preload")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}
