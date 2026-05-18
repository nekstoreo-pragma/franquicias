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
}
