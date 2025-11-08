package org.example.gold.model;



import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.List;

/**
 * Represents a product category in the system.
 *
 * Each category can have multiple products.
 */
@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    /** The unique identifier of the category. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The category name. */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /** A brief description of the category. */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Whether this category is active or not. */
    @Column(nullable = false)
    private boolean active = true;

    /** Timestamp for when the category was created. */
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /** Timestamp for when the category was last updated. */
    private Instant updatedAt;

    /** The list of products that belong to this category. */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    private List<Product> products;

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
