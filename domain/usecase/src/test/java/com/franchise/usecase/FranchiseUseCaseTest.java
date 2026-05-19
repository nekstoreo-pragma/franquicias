package com.franchise.usecase;

import com.franchise.model.branch.Branch;
import com.franchise.model.branch.gateways.BranchRepository;
import com.franchise.model.franchise.BranchTopProduct;
import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import com.franchise.model.product.Product;
import com.franchise.model.product.gateways.ProductRepository;
import com.franchise.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    @Test
    void create_delegatesToRepositoryAndReturnsSavedEntity() {
        Franchise input = Franchise.builder().name("Burger King").build();
        Franchise saved = Franchise.builder().id("uuid-1").name("Burger King").build();
        when(franchiseRepository.save(any())).thenReturn(Mono.just(saved));

        StepVerifier.create(franchiseUseCase.create(input))
                .expectNextMatches(f -> "uuid-1".equals(f.getId()) && "Burger King".equals(f.getName()))
                .verifyComplete();

        verify(franchiseRepository).save(input);
    }

    @Test
    void updateName_franchiseFound_returnsUpdated() {
        Franchise existing = Franchise.builder().id("f-1").name("Old Name").build();
        Franchise updated = existing.toBuilder().name("New Name").build();
        when(franchiseRepository.findById("f-1")).thenReturn(Mono.just(existing));
        when(franchiseRepository.update(any())).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseUseCase.updateName("f-1", "New Name"))
                .expectNextMatches(f -> "New Name".equals(f.getName()))
                .verifyComplete();
    }

    @Test
    void updateName_franchiseNotFound_propagatesError() {
        when(franchiseRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateName("missing", "New Name"))
                .expectError(NoSuchElementException.class)
                .verify();
    }

    @Test
    void updateName_blankName_returnsIllegalArgumentError() {
        StepVerifier.create(franchiseUseCase.updateName("f-1", ""))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getTopProductPerBranch_returnsBestProductPerBranch() {
        Franchise franchise = Franchise.builder().id("f-1").name("BK").build();
        Branch b1 = Branch.builder().id("b-1").name("Norte").franchiseId("f-1").build();
        Branch b2 = Branch.builder().id("b-2").name("Sur").franchiseId("f-1").build();
        Product p1 = Product.builder().id("p-1").name("Whopper").stock(50).branchId("b-1").build();
        Product p2 = Product.builder().id("p-2").name("Fries").stock(10).branchId("b-1").build();
        Product p3 = Product.builder().id("p-3").name("Shake").stock(30).branchId("b-2").build();

        when(franchiseRepository.findById("f-1")).thenReturn(Mono.just(franchise));
        when(branchRepository.findByFranchiseId("f-1")).thenReturn(Flux.just(b1, b2));
        when(productRepository.findByBranchId("b-1")).thenReturn(Flux.just(p1, p2));
        when(productRepository.findByBranchId("b-2")).thenReturn(Flux.just(p3));

        StepVerifier.create(franchiseUseCase.getTopProductPerBranch("f-1"))
                .expectNextMatches(list -> list.size() == 2
                        && list.stream().anyMatch(r -> "p-1".equals(r.getTopProduct().getId()))
                        && list.stream().anyMatch(r -> "p-3".equals(r.getTopProduct().getId())))
                .verifyComplete();
    }

    @Test
    void getTopProductPerBranch_branchWithNoProducts_isOmitted() {
        Franchise franchise = Franchise.builder().id("f-1").name("BK").build();
        Branch b1 = Branch.builder().id("b-1").name("Norte").franchiseId("f-1").build();

        when(franchiseRepository.findById("f-1")).thenReturn(Mono.just(franchise));
        when(branchRepository.findByFranchiseId("f-1")).thenReturn(Flux.just(b1));
        when(productRepository.findByBranchId("b-1")).thenReturn(Flux.empty());

        StepVerifier.create(franchiseUseCase.getTopProductPerBranch("f-1"))
                .expectNextMatches(List::isEmpty)
                .verifyComplete();
    }

    @Test
    void getTopProductPerBranch_franchiseNotFound_propagatesError() {
        when(franchiseRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.getTopProductPerBranch("missing"))
                .expectError(NoSuchElementException.class)
                .verify();
    }
}
