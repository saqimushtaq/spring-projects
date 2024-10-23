package com.saqib.elasticsearch.services;

import com.saqib.elasticsearch.entity.Product;
import com.saqib.elasticsearch.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
  final ProductRepository productRepository;

  public Iterable<Product> findAll() {
    return productRepository.findAll();
  }

  public Product findById(Long id) {
    return productRepository.findById(id).orElse(null);
  }

  public Product save(Product product) {
    return productRepository.save(product);
  }

  public Product update(Long id, Product product) {
    return productRepository.findById(id).map(old -> {
      product.setId(old.getId());
      return productRepository.save(product);
    }).orElse(null);
  }

  public void delete(Long id) {
    productRepository.deleteById(id);
  }

  public Iterable<Product> search(String keyword) {
    return productRepository.searchByNameContainingIgnoreCase(keyword);
  }
}
