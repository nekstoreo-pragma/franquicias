package com.franchise.usecase;

import com.franchise.model.branch.Branch;
import com.franchise.model.branch.gateways.BranchRepository;
import com.franchise.model.product.Product;
import com.franchise.model.product.gateways.ProductRepository;
import com.franchise.usecase.product.ProductUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    @Test
    void addProduct_branchExists_savesProductWithBranchId() {
        Branch branch = Branch.builder().id("b-1").name("Branch A").build();
        Product input = Product.builder().name("Burger").stock(10).build();
        Product saved = Product.builder().id("p-1").name("Burger").stock(10).branchId("b-1").build();

        when(branchRepository.findById("b-1")).thenReturn(Mono.just(branch));
        when(productRepository.save(any())).thenReturn(Mono.just(saved));

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);

        StepVerifier.create(productUseCase.addProduct("b-1", input))
                .expectNextMatches(p -> "p-1".equals(p.getId()) && "b-1".equals(p.getBranchId()))
                .verifyComplete();

        verify(productRepository).save(captor.capture());
        assertEquals("b-1", captor.getValue().getBranchId());
        assertEquals("Burger", captor.getValue().getName());
    }

    @Test
    void addProduct_branchNotFound_propagatesError() {
        when(branchRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.addProduct("missing", Product.builder().name("X").stock(5).build()))
                .expectError(NoSuchElementException.class)
                .verify();
    }

    @Test
    void addProduct_negativeStock_returnsIllegalArgumentError() {
        StepVerifier.create(productUseCase.addProduct("b-1", Product.builder().name("X").stock(-1).build()))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void updateStock_productFound_returnsUpdated() {
        Product existing = Product.builder().id("p-1").name("Burger").stock(10).branchId("b-1").build();
        Product updated = existing.toBuilder().stock(50).build();
        when(productRepository.findById("p-1")).thenReturn(Mono.just(existing));
        when(productRepository.update(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.updateStock("p-1", 50))
                .expectNextMatches(p -> p.getStock() == 50)
                .verifyComplete();
    }

    @Test
    void updateStock_productNotFound_propagatesError() {
        when(productRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.updateStock("missing", 10))
                .expectError(NoSuchElementException.class)
                .verify();
    }

    @Test
    void updateStock_negativeStock_returnsIllegalArgumentError() {
        StepVerifier.create(productUseCase.updateStock("p-1", -5))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void removeProduct_callsDelete() {
        when(productRepository.delete("p-1")).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.removeProduct("p-1"))
                .verifyComplete();

        verify(productRepository).delete("p-1");
    }
}
