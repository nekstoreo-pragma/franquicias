package com.franchise.model;

import com.franchise.model.product.Product;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    @Test
    void builder_setsStockAndBranchId() {
        Product product = Product.builder()
                .id("p1")
                .name("Test Product")
                .stock(100)
                .branchId("b1")
                .build();

        assertEquals(100, product.getStock());
        assertEquals("b1", product.getBranchId());
    }

    @Test
    void allArgsConstructor_setsAllFields() {
        Product product = new Product("p1", "Test Product", 100, "b1");

        assertEquals("p1", product.getId());
        assertEquals("Test Product", product.getName());
        assertEquals(100, product.getStock());
        assertEquals("b1", product.getBranchId());
    }
}
