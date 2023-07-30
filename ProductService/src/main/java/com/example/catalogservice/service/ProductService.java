package com.example.catalogservice.service;

import com.example.catalogservice.entity.Product;
import com.example.catalogservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Product getProduct(int productId) {
        return productRepository.findById(productId).get();
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
}
