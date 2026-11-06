package com.codegym.casestudymodul4.repository;

import com.codegym.casestudymodul4.entity.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  List<Product> findByActiveTrue();

  List<Product> findByNameContainingIgnoreCaseAndActiveTrue(String name);

  List<Product> findByCategoryIdAndActiveTrue(Long categoryId);

  List<Product> findByActiveTrueAndPriceBetween(BigDecimal min, BigDecimal max);
}
