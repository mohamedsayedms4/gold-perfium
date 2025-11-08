package org.example.gold.repository;

import org.example.gold.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

//    /** Find products by active status. */
//    List<Product> findByActiveTrue();
//
//    /** Find products by category ID. */
//    List<Product> findByCategoryId(Long categoryId);

    Page<Product> findByActiveTrue(Pageable pageable);
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);
}