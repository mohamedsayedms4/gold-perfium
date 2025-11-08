package org.example.gold.service;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.Category;
import org.example.gold.model.Product;
import org.example.gold.repository.CategoryRepository;
import org.example.gold.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
//
//    public List<Product> findAll() {
//        return productRepository.findAll();
//    }

//    public List<Product> findActiveProducts() {
//        return productRepository.findByActiveTrue();
//    }
//
//    public List<Product> findByCategory(Long categoryId) {
//        return productRepository.findByCategoryId(categoryId);
//    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    // إضافة دوال جديدة للـ pagination
    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    public Page<Product> findByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable);
    }
}
