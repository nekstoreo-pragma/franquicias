package com.franchise.model;

import com.franchise.model.branch.Branch;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BranchTest {

    @Test
    void builder_setsFranchiseId() {
        Branch branch = Branch.builder()
                .id("b1")
                .name("Test Branch")
                .franchiseId("f1")
                .build();

        assertEquals("f1", branch.getFranchiseId());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        Branch branch = new Branch("b1", "Test Branch", "f1");

        assertEquals("b1", branch.getId());
        assertEquals("Test Branch", branch.getName());
        assertEquals("f1", branch.getFranchiseId());
    }
}
