package com.franchise.usecase;

import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import com.franchise.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

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
}
