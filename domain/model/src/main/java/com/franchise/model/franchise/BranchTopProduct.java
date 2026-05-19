package com.franchise.model.franchise;

import com.franchise.model.branch.Branch;
import com.franchise.model.product.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BranchTopProduct {
    private final Branch branch;
    private final Product topProduct;
}
