package org.example.gold.service;

import lombok.RequiredArgsConstructor;
import org.example.gold.model.Order;
import org.example.gold.model.OrderItem;
import org.example.gold.model.Product;
import org.example.gold.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.example.gold.repository.OrderRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service layer for handling business logic related to orders.
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final ProductRepository productRepository;

    /**
     * Retrieves all orders from the database.
     */
    public Page<Order> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    /**
     * Retrieves an order by its ID.
     */
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * Saves a new order or updates an existing one.
     */
    @Transactional
    public Order save(Order order) {
        // ✅ التأكد من تحميل المنتجات لكل OrderItem
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null && item.getProduct().getId() != null) {
                // ✅ إعادة تحميل المنتج من قاعدة البيانات
                Product product = productRepository.findById(item.getProduct().getId())
                        .orElseThrow(() -> new RuntimeException(
                                "Product not found with ID: " + item.getProduct().getId()));

                item.setProduct(product);
                System.out.println("✅ Product loaded: " + product.getName());
            } else {
                System.err.println("⚠️ OrderItem has no valid product!");
            }
            item.setOrder(order);
        }

        order.calculateTotal();
        Order savedOrder = orderRepository.save(order);

        // ✅ إرسال إيميل فيه تفاصيل الطلب
        sendOrderEmail(savedOrder);

        return savedOrder;
    }

    /**
     * Sends order confirmation email
     */
    private void sendOrderEmail(Order savedOrder) {
        try {
            String subject = "📦 تم استلام طلب جديد رقم #" + savedOrder.getId();

            String formattedDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneId.systemDefault())
                    .format(savedOrder.getCreatedAt());

            StringBuilder itemsTable = new StringBuilder();
            itemsTable.append("""
                <table style="border-collapse:collapse;width:100%;margin-top:15px;font-family:Arial,sans-serif">
                    <thead>
                        <tr style="background-color:#f2f2f2;text-align:center">
                            <th style="padding:10px;border:1px solid #ddd">الصورة</th>
                            <th style="padding:10px;border:1px solid #ddd">المنتج</th>
                            <th style="padding:10px;border:1px solid #ddd">الكمية</th>
                            <th style="padding:10px;border:1px solid #ddd">السعر الفردي</th>
                            <th style="padding:10px;border:1px solid #ddd">الإجمالي الفرعي</th>
                        </tr>
                    </thead>
                    <tbody>
            """);

            for (OrderItem item : savedOrder.getItems()) {
                String productName = "غير محدد";
                String imageUrl = "https://via.placeholder.com/80x80.png?text=No+Image";

                if (item.getProduct() != null) {
                    productName = item.getProduct().getName() != null ?
                            item.getProduct().getName() : "غير محدد";

                    if (item.getProduct().getImageUrl() != null &&
                            !item.getProduct().getImageUrl().isBlank()) {
                        imageUrl = item.getProduct().getImageUrl();
                    }

                    System.out.println("📧 Email - Product: " + productName +
                            ", Image: " + imageUrl);
                } else {
                    System.err.println("⚠️ Email - Product is NULL for item!");
                }

                itemsTable.append(String.format("""
                    <tr style="text-align:center">
                        <td style="padding:8px;border:1px solid #ddd">
                            <img src="%s" alt="صورة المنتج" style="width:60px;height:60px;border-radius:8px;object-fit:cover;">
                        </td>
                        <td style="padding:8px;border:1px solid #ddd">%s</td>
                        <td style="padding:8px;border:1px solid #ddd">%d</td>
                        <td style="padding:8px;border:1px solid #ddd">%.2f جنيه</td>
                        <td style="padding:8px;border:1px solid #ddd">%.2f جنيه</td>
                    </tr>
                """,
                        imageUrl,
                        productName,
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal()
                ));
            }

            itemsTable.append("""
                    </tbody>
                </table>
            """);

            String body = String.format("""
                <div style="font-family:Arial,sans-serif;color:#333;line-height:1.6;max-width:800px;margin:0 auto">
                    <h2 style="color:#2c3e50;text-align:center;background:#f8f9fa;padding:15px;border-radius:8px">
                        📦 تفاصيل الطلب الجديد
                    </h2>
                    <div style="margin-top:20px;background:#ffffff;padding:20px;border:1px solid #e0e0e0;border-radius:8px">
                        <p><strong>🆔 رقم الطلب:</strong> %d</p>
                        <p><strong>👤 اسم العميل:</strong> %s</p>
                        <p><strong>📧 البريد الإلكتروني:</strong> %s</p>
                        <p><strong>📱 رقم الهاتف:</strong> %s</p>
                        <p><strong>📍 العنوان:</strong> %s</p>
                        <p><strong>📊 الحالة:</strong> <span style="color:#27ae60;font-weight:bold">%s</span></p>
                        <p><strong>📅 تاريخ الإنشاء:</strong> %s</p>
                    </div>
                    <hr style="border:none;border-top:2px solid #e0e0e0;margin:20px 0"/>
                    %s
                    <div style="text-align:center;margin-top:30px;background:#27ae60;color:white;padding:20px;border-radius:8px">
                        <h3 style="margin:0;font-size:24px">
                            💰 الإجمالي الكلي: %.2f جنيه
                        </h3>
                    </div>
                    <hr style="border:none;border-top:2px solid #e0e0e0;margin:20px 0"/>
                    <p style="font-size:13px;color:#888;text-align:center;margin-top:20px">
                        تم إرسال هذا الإشعار تلقائيًا من نظام الطلبات الذهبي 💎
                    </p>
                </div>
            """,
                    savedOrder.getId(),
                    savedOrder.getCustomerName(),
                    savedOrder.getCustomerEmail(),
                    savedOrder.getCustomerPhone(),
                    savedOrder.getShippingAddress(),
                    savedOrder.getStatus(),
                    formattedDate,
                    itemsTable,
                    savedOrder.getTotalAmount()
            );

            emailService.sendEmail("mahmoudkhakifa140@gmail.com", subject, body);
            emailService.sendEmail("ms4002@fayoum.edu.eg", subject, body);

            System.out.println("✅ Email sent successfully for Order #" + savedOrder.getId());

        } catch (Exception e) {
            System.err.println("⚠️ فشل إرسال الإيميل: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes an order by ID.
     */
    public void deleteById(Long id) {
        orderRepository.deleteById(id);
    }
}