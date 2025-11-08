package org.example.gold.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Represents a single item (product + quantity) in an order.
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    /** The unique identifier of the order item. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The associated product for this item. */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Quantity of this product in the order. */
    @Column(nullable = false)
    private int quantity;

    /** Price per unit at the time of order. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    /** Subtotal (unitPrice * quantity). */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /** The order that this item belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    private Order order;

    /** Calculates the subtotal automatically. */
    @PrePersist
    @PreUpdate
    public void calculateSubtotal() {
        if (unitPrice != null) {
            this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
