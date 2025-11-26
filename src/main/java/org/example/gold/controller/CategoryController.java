package org.example.gold.controller;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.Category;
import org.example.gold.service.CategoryService;
import org.example.gold.service.ImageUploadUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
//@CrossOrigin("*")
public class CategoryController {
    private final CategoryService categoryService;
    private final ImageUploadUtil imageUploadUtil;

    @GetMapping
    public Page<Category> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findAll(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // إضافة endpoint جديد للحصول على الكاتجوري مع المنتجات
    @GetMapping("/{id}/products")
    public ResponseEntity<Category> getCategoryWithProducts(@PathVariable Long id) {
        return categoryService.findByIdWithProducts(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/{id}/image")
    public ResponseEntity<?> uploadCategoryImage(@PathVariable Long id,
                                                 @RequestParam("image") MultipartFile image) {
        return categoryService.findById(id).map(category -> {
            String imageUrl = imageUploadUtil.saveImage(image);
            category.setImage(imageUrl);
            categoryService.save(category);
            return ResponseEntity.ok("Image uploaded successfully: " + imageUrl);
        }).orElse(ResponseEntity.notFound().build());
    }

}