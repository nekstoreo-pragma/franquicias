package com.franchise.usecase;

import com.franchise.model.branch.Branch;
import com.franchise.model.branch.gateways.BranchRepository;
import com.franchise.model.franchise.Franchise;
import com.franchise.model.franchise.gateways.FranchiseRepository;
import com.franchise.usecase.branch.BranchUseCase;
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
class BranchUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    @Test
    void addBranch_franchiseExists_savesBranchWithFranchiseId() {
        Franchise franchise = Franchise.builder().id("f-1").name("Burger King").build();
        Branch input = Branch.builder().name("Branch A").build();
        Branch saved = Branch.builder().id("b-1").name("Branch A").franchiseId("f-1").build();

        when(franchiseRepository.findById("f-1")).thenReturn(Mono.just(franchise));
        when(branchRepository.save(any())).thenReturn(Mono.just(saved));

        ArgumentCaptor<Branch> captor = ArgumentCaptor.forClass(Branch.class);

        StepVerifier.create(branchUseCase.addBranch("f-1", input))
                .expectNextMatches(b -> "b-1".equals(b.getId()) && "f-1".equals(b.getFranchiseId()))
                .verifyComplete();

        verify(branchRepository).save(captor.capture());
        assertEquals("f-1", captor.getValue().getFranchiseId());
        assertEquals("Branch A", captor.getValue().getName());
    }

    @Test
    void addBranch_franchiseNotFound_propagatesError() {
        when(franchiseRepository.findById("missing")).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.addBranch("missing", Branch.builder().name("X").build()))
                .expectError(NoSuchElementException.class)
                .verify();
    }
}
