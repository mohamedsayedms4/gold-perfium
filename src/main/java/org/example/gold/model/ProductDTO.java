package org.example.gold.model;


import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private int quantity;
    private boolean active;
    private List<String> images;
    private String categoryName; // فقط اسم الفئة بدلاً من الكائن كاملاً
    private Instant createdAt;
    private Instant updatedAt;
}