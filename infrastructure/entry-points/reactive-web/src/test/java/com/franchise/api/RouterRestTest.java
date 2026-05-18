package com.franchise.api;

import com.franchise.api.dto.AddBranchRequest;
import com.franchise.api.dto.CreateFranchiseRequest;
import com.franchise.model.branch.Branch;
import com.franchise.model.franchise.Franchise;
import com.franchise.usecase.branch.BranchUseCase;
import com.franchise.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class, BranchHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    @MockitoBean
    private BranchUseCase branchUseCase;

    @Test
    void createFranchise_validRequest_returns201() {
        when(franchiseUseCase.create(any())).thenReturn(
                Mono.just(Franchise.builder().id("id-1").name("Test").build()));

        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranchiseRequest("Test"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("id-1")
                .jsonPath("$.name").isEqualTo("Test");
    }

    @Test
    void createFranchise_blankName_returns400() {
        webTestClient.post()
                .uri("/api/v1/franchises")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new CreateFranchiseRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addBranch_validRequest_returns201() {
        when(branchUseCase.addBranch(eq("f-1"), any())).thenReturn(
                Mono.just(Branch.builder().id("b-1").name("Branch A").franchiseId("f-1").build()));

        webTestClient.post()
                .uri("/api/v1/franchises/f-1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddBranchRequest("Branch A"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("b-1")
                .jsonPath("$.franchiseId").isEqualTo("f-1");
    }

    @Test
    void addBranch_franchiseNotFound_returns404() {
        when(branchUseCase.addBranch(eq("missing"), any()))
                .thenReturn(Mono.error(new NoSuchElementException("Franchise not found")));

        webTestClient.post()
                .uri("/api/v1/franchises/missing/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddBranchRequest("Branch A"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void addBranch_blankName_returns400() {
        webTestClient.post()
                .uri("/api/v1/franchises/f-1/branches")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddBranchRequest(""))
                .exchange()
                .expectStatus().isBadRequest();
    }
}
