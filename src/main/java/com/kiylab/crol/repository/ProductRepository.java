package com.kiylab.crol.repository;

import com.kiylab.crol.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

// Product 엔티티용 Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

