package com.saqib.elasticsearch.controllers;

import com.saqib.elasticsearch.entity.Product;
import com.saqib.elasticsearch.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {
  @Autowired
  private ProductService productService;

  @GetMapping
  public Iterable<Product> getProducts() {
    return productService.findAll();
  }

  @GetMapping("/search")
  public Iterable<Product> searchProducts(@RequestParam String keyword) {
    return productService.search(keyword);
  }

  @GetMapping("{id}")
  public Product getProduct(@PathVariable Long id) {
    return productService.findById(id);
  }

  @PostMapping
  public Product addProduct(@RequestBody Product product) {
    return productService.save(product);
  }

  @PutMapping("{id}")
  public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
    return productService.update(id, product);
  }

  @DeleteMapping("{id}")
  public void deleteProduct(@PathVariable Long id) {
    productService.delete(id);
  }
}
