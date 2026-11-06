package com.codegym.casestudymodul4.repository;

import com.codegym.casestudymodul4.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {}
