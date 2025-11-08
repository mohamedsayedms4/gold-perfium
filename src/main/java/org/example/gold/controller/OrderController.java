package org.example.gold.controller;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.Order;
import org.example.gold.model.OrderItem;
import org.example.gold.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;

//    @GetMapping
//    public Page<Order> getAllOrders(@RequestParam(defaultValue = "0") int page,
//                                    @RequestParam(defaultValue = "10") int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        return orderService.findAll(pageable);
//    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        // ✅ تأكد أن كل OrderItem يحتوي على product.id
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                return ResponseEntity.badRequest().build();
            }
        }

        Order saved = orderService.save(order);
        return ResponseEntity.ok(saved);
    }
}
