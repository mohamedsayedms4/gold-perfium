package org.example.gold.service;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.Category;
import org.example.gold.model.Product;
import org.example.gold.model.ProductDTO;
import org.example.gold.repository.CategoryRepository;
import org.example.gold.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> productsPage = productRepository.findAllWithCategory(pageable);
        List<Product> products = productsPage.getContent();

        if (!products.isEmpty()) {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            productRepository.findByIdInWithImages(productIds);
        }

        List<ProductDTO> dtos = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findActiveProducts(Pageable pageable) {
        Page<Product> productsPage = productRepository.findByActive(true, pageable);
        List<Product> products = productsPage.getContent();

        if (!products.isEmpty()) {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            productRepository.findByIdInWithImages(productIds);
        }

        List<ProductDTO> dtos = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> findByCategory(Long categoryId, Pageable pageable) {
        Page<Product> productsPage = productRepository.findByCategoryId(categoryId, pageable);
        List<Product> products = productsPage.getContent();

        if (!products.isEmpty()) {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            productRepository.findByIdInWithImages(productIds);
        }

        List<ProductDTO> dtos = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        Page<Product> productsPage = productRepository.searchByNameOrDescription(keyword, pageable);
        List<Product> products = productsPage.getContent();

        if (!products.isEmpty()) {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            productRepository.findByIdInWithImages(productIds);
        }

        List<ProductDTO> dtos = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Optional<ProductDTO> findById(Long id) {
        return productRepository.findByIdWithCategoryAndImages(id)
                .map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Optional<Product> findProductEntityById(Long id) {
        return productRepository.findByIdWithCategoryAndImages(id);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ProductDTO> searchProductsInCategory(Long categoryId, String keyword, Pageable pageable) {
        Page<Product> productsPage = productRepository.searchInCategory(categoryId, keyword, pageable);
        List<Product> products = productsPage.getContent();

        if (!products.isEmpty()) {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toList());

            productRepository.findByIdInWithImages(productIds);
        }

        List<ProductDTO> dtos = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, productsPage.getTotalElements());
    }


    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found: " + id));
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setQuantity(product.getQuantity());
        dto.setActive(product.isActive());
        dto.setImages(product.getImages());

        if (product.getCategory() != null) {
            dto.setCategoryName(product.getCategory().getName());
        }

        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());
        return dto;
    }
}