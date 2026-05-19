package com.franchise.api;

import com.franchise.api.dto.AddBranchRequest;
import com.franchise.api.dto.AddProductRequest;
import com.franchise.api.dto.CreateFranchiseRequest;
import com.franchise.api.dto.UpdateNameRequest;
import com.franchise.api.dto.UpdateStockRequest;
import com.franchise.model.branch.Branch;
import com.franchise.model.franchise.Franchise;
import com.franchise.model.product.Product;
import com.franchise.usecase.branch.BranchUseCase;
import com.franchise.usecase.franchise.FranchiseUseCase;
import com.franchise.usecase.product.ProductUseCase;
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

@ContextConfiguration(classes = {RouterRest.class, FranchiseHandler.class, BranchHandler.class, ProductHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private FranchiseUseCase franchiseUseCase;

    @MockitoBean
    private BranchUseCase branchUseCase;

    @MockitoBean
    private ProductUseCase productUseCase;

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

    @Test
    void addProduct_validRequest_returns201() {
        when(productUseCase.addProduct(eq("b-1"), any())).thenReturn(
                Mono.just(Product.builder().id("p-1").name("Burger").stock(10).branchId("b-1").build()));

        webTestClient.post()
                .uri("/api/v1/branches/b-1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddProductRequest("Burger", 10))
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo("p-1")
                .jsonPath("$.branchId").isEqualTo("b-1");
    }

    @Test
    void addProduct_branchNotFound_returns404() {
        when(productUseCase.addProduct(eq("missing"), any()))
                .thenReturn(Mono.error(new NoSuchElementException("Branch not found")));

        webTestClient.post()
                .uri("/api/v1/branches/missing/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddProductRequest("Burger", 10))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void addProduct_blankName_returns400() {
        webTestClient.post()
                .uri("/api/v1/branches/b-1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddProductRequest("", 10))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void addProduct_negativeStock_returns400() {
        webTestClient.post()
                .uri("/api/v1/branches/b-1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new AddProductRequest("Burger", -1))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void deleteProduct_returns204() {
        when(productUseCase.removeProduct("p-1")).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/v1/products/p-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateProductStock_validRequest_returns200() {
        Product updated = Product.builder().id("p-1").name("Burger").stock(50).branchId("b-1").build();
        when(productUseCase.updateStock(eq("p-1"), eq(50))).thenReturn(Mono.just(updated));

        webTestClient.patch()
                .uri("/api/v1/products/p-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(50))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stock").isEqualTo(50);
    }

    @Test
    void updateProductStock_productNotFound_returns404() {
        when(productUseCase.updateStock(eq("missing"), eq(10)))
                .thenReturn(Mono.error(new NoSuchElementException("Product not found")));

        webTestClient.patch()
                .uri("/api/v1/products/missing/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(10))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateProductStock_negativeStock_returns400() {
        webTestClient.patch()
                .uri("/api/v1/products/p-1/stock")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateStockRequest(-1))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void updateFranchiseName_validRequest_returns200() {
        when(franchiseUseCase.updateName(eq("f-1"), eq("New Name"))).thenReturn(
                Mono.just(Franchise.builder().id("f-1").name("New Name").build()));

        webTestClient.patch()
                .uri("/api/v1/franchises/f-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Name"))
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("New Name");
    }

    @Test
    void updateFranchiseName_notFound_returns404() {
        when(franchiseUseCase.updateName(eq("missing"), any()))
                .thenReturn(Mono.error(new NoSuchElementException("Franchise not found")));

        webTestClient.patch()
                .uri("/api/v1/franchises/missing/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("X"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateBranchName_validRequest_returns200() {
        when(branchUseCase.updateName(eq("b-1"), eq("New Branch"))).thenReturn(
                Mono.just(Branch.builder().id("b-1").name("New Branch").franchiseId("f-1").build()));

        webTestClient.patch()
                .uri("/api/v1/branches/b-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Branch"))
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("New Branch");
    }

    @Test
    void updateBranchName_notFound_returns404() {
        when(branchUseCase.updateName(eq("missing"), any()))
                .thenReturn(Mono.error(new NoSuchElementException("Branch not found")));

        webTestClient.patch()
                .uri("/api/v1/branches/missing/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("X"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateProductName_validRequest_returns200() {
        when(productUseCase.updateName(eq("p-1"), eq("New Product"))).thenReturn(
                Mono.just(Product.builder().id("p-1").name("New Product").stock(10).branchId("b-1").build()));

        webTestClient.patch()
                .uri("/api/v1/products/p-1/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("New Product"))
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$.name").isEqualTo("New Product");
    }

    @Test
    void updateProductName_notFound_returns404() {
        when(productUseCase.updateName(eq("missing"), any()))
                .thenReturn(Mono.error(new NoSuchElementException("Product not found")));

        webTestClient.patch()
                .uri("/api/v1/products/missing/name")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UpdateNameRequest("X"))
                .exchange()
                .expectStatus().isNotFound();
    }
}
