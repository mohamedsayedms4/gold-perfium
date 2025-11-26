package org.example.gold.model;

import lombok.Data;
import java.time.Instant;

@Data
public class CategoryDTO {
    private Long id;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    // يمكن إضافة عدد المنتجات إذا أردت
    private Integer productsCount;

    private String image;

}