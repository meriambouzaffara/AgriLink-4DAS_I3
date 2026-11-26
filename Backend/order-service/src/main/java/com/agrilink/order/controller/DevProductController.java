package com.agrilink.order.controller;

import com.agrilink.order.client.MockProductClient;
import com.agrilink.order.client.ProductInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Profile("dev-mock")
@RequestMapping("/api/dev/products")
public class DevProductController {
    private final MockProductClient products;

    public DevProductController(MockProductClient products) {
        this.products = products;
    }

    @GetMapping
    public List<ProductInfo> list() {
        return products.listProducts();
    }
}
