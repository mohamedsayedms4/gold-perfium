package org.example.gold.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * Represents a product entity stored in the database.
 *
 * Each product belongs to one category.
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /** The unique identifier of the product. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The product name. */
    @Column(nullable = false, length = 100)
    private String name;

    /** Detailed description of the product. */
    @Column(columnDefinition = "TEXT")
    private String description;


    /** Product price. */
    @Column(nullable = false)
    private Double price;

    /** Quantity available in stock. */
    @Column(nullable = false)
    private int quantity;

    /** Whether the product is active and available for sale. */
    @Column(nullable = false)
    private boolean active = true;

    /** Image URL or path. */
    private String imageUrl;

    /** The category to which this product belongs. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Category category;

    /** Creation timestamp. */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** Last update timestamp. */
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
