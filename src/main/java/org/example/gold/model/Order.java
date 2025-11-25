    package org.example.gold.model;

    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import lombok.*;

    import java.math.BigDecimal;
    import java.time.Instant;
    import java.util.ArrayList;
    import java.util.List;

    /**
     * Represents a customer's order in the system.
     *
     * Each order can contain multiple order items (products with quantities).
     */
    @Entity
    @Table(name = "orders")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Order {

        /** The unique identifier of the order. */
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        /** The customer's full name. */
        @Column(nullable = false, length = 100)
        private String customerName;

        /** Customer's phone number. */
        @Column(length = 20)
        private String customerPhone;

        /** Shipping address for the order. */
        @Column(columnDefinition = "TEXT")
        private String shippingAddress;

        /** Total price of the order. */
        @Column(nullable = false, precision = 10, scale = 2)
        private BigDecimal totalAmount;

        /** The current status of the order (e.g., NEW, SHIPPED, DELIVERED, CANCELED). */
        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private OrderStatus status = OrderStatus.NEW;

        /** Timestamp when the order was created. */
        @Column(nullable = false, updatable = false)
        private Instant createdAt;

        /** Timestamp when the order was last updated. */
        private Instant updatedAt;

        /** The list of items included in this order. */
        @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        @Builder.Default
        private List<OrderItem> items = new ArrayList<>();

        /** Sets timestamps automatically before saving. */
        @PrePersist
        protected void onCreate() {
            createdAt = Instant.now();
            updatedAt = createdAt;
        }

        /** Updates timestamp before updating entity. */
        @PreUpdate
        protected void onUpdate() {
            updatedAt = Instant.now();
        }

        /**
         * Calculates the total order amount based on the order items.
         */
        public void calculateTotal() {
            this.totalAmount = items.stream()
                    .map(OrderItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
