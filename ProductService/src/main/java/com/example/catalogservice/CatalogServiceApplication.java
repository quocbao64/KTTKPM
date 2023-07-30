package com.example.catalogservice;

import com.example.catalogservice.entity.Product;
import com.example.catalogservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CatalogServiceApplication implements CommandLineRunner {
    @Autowired
    private ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(CatalogServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        productService.saveProduct(new Product("Product 1", Double.parseDouble(String.valueOf(Math.random() * (500 - 50)) + 50)));
        productService.saveProduct(new Product("Product 2", Double.parseDouble(String.valueOf(Math.random() * (500 - 50)) + 50)));
        productService.saveProduct(new Product("Product 3", Double.parseDouble(String.valueOf(Math.random() * (500 - 50)) + 50)));
        productService.saveProduct(new Product("Product 4", Double.parseDouble(String.valueOf(Math.random() * (500 - 50)) + 50)));
    }
}
