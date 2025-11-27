package com.microservice.cart.clients;

import com.microservice.cart.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-product", url = "${product.service.url:http://fotomarwmsdb.ddns.net:8083}", path = "/products")
public interface ProductClient {

    @GetMapping("/{id}")
    ProductDto findById(@PathVariable Long id);
}
