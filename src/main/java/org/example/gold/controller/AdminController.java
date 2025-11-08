package org.example.gold.controller;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.Category;
import org.example.gold.model.Order;
import org.example.gold.model.Product;
import org.example.gold.service.ImageUploadUtil;
import org.example.gold.service.OrderService;
import org.example.gold.service.ProductService;
import org.example.gold.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AdminController {

    private final OrderService orderService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final ImageUploadUtil imageUploadUtil;

    // ========================= Orders =========================
    @GetMapping("/orders")
    public Page<Order> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderService.findAll(pageable);
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/orders/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return ResponseEntity.ok(orderService.save(order));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ========================= Products =========================
    @PostMapping("/products/upload")
    public ResponseEntity<Product> createProductWithImage(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Integer quantity,
            @RequestParam boolean active,
            @RequestParam Long categoryId,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            imageUrl = imageUploadUtil.saveImage(image);
        }

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setActive(active);
        product.setImageUrl(imageUrl);
        product.setCategory(productService.getCategoryById(categoryId));

        return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                                 @RequestParam String name,
                                                 @RequestParam String description,
                                                 @RequestParam Double price,
                                                 @RequestParam Integer quantity,
                                                 @RequestParam boolean active,
                                                 @RequestParam Long categoryId,
                                                 @RequestParam(value = "image", required = false) MultipartFile image) {

        return productService.findById(id)
                .map(existing -> {
                    existing.setName(name);
                    existing.setDescription(description);
                    existing.setPrice(price);
                    existing.setQuantity(quantity);
                    existing.setActive(active);
                    existing.setCategory(productService.getCategoryById(categoryId));

                    if (image != null && !image.isEmpty()) {
                        existing.setImageUrl(imageUploadUtil.saveImage(image));
                    }

                    return ResponseEntity.ok(productService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/products")
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productService.findAll(pageable);
    }


    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // ========================= Categories =========================
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        return ResponseEntity.ok(categoryService.save(category));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category updated) {
        return categoryService.findById(id)
                .map(existing -> {
                    existing.setName(updated.getName());
                    existing.setDescription(updated.getDescription());
                    existing.setActive(updated.isActive());
                    return ResponseEntity.ok(categoryService.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }



    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public Page<Category> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return categoryService.findAll(pageable);
    }
}
