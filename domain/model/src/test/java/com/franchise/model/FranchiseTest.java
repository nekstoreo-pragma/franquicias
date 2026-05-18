package com.franchise.model;

import com.franchise.model.franchise.Franchise;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FranchiseTest {

    @Test
    void builder_setsAllFields() {
        Franchise franchise = Franchise.builder()
                .id("f1")
                .name("Test Franchise")
                .build();

        assertEquals("f1", franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        Franchise franchise = new Franchise("f1", "Test Franchise");

        assertEquals("f1", franchise.getId());
        assertEquals("Test Franchise", franchise.getName());
    }
}
