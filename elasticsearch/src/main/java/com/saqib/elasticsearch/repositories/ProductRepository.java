package com.saqib.elasticsearch.repositories;

import com.saqib.elasticsearch.entity.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ProductRepository extends ElasticsearchRepository<Product, Long> {
  Iterable<Product> searchByNameContainingIgnoreCase(String keyword);
}
