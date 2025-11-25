package org.example.gold.repository;

import org.example.gold.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // استعلام واحد يجلب Product + Category + Images
    @Query("SELECT DISTINCT p FROM Product p " +
            "LEFT JOIN FETCH p.category " +
            "LEFT JOIN FETCH p.images " +
            "WHERE p.id = :id")
    Optional<Product> findByIdWithCategoryAndImages(@Param("id") Long id);

    // للـ Pagination - نحتاج استعلامين (مشكلة Hibernate المعروفة)
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p")
    Page<Product> findAllWithCategory(Pageable pageable);

    // استعلام إضافي لجلب الصور للمنتجات المُحملة
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.images WHERE p.id IN :ids")
    List<Product> findByIdInWithImages(@Param("ids") List<Long> ids);

    // البحث عن المنتجات النشطة
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category WHERE p.active = :active",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p WHERE p.active = :active")
    Page<Product> findByActive(@Param("active") boolean active, Pageable pageable);

    // البحث حسب الفئة
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.id = :categoryId",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // البحث بالاسم أو الوصف
    @Query(value = "SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.category " +
            "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT COUNT(DISTINCT p) FROM Product p " +
                    "WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                    "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> searchByNameOrDescription(@Param("keyword") String keyword, Pageable pageable);
}