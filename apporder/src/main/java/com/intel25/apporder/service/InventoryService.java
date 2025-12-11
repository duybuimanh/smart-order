package com.intel25.apporder.service;

import com.intel25.apporder.entity.Order;
import com.intel25.apporder.entity.Product;
import com.intel25.apporder.repository.OrderRepository;
import com.intel25.apporder.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void updateInventory(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

            Product product = productRepository.findById(order.getProductId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + order.getProductId()));

            // Kiểm tra tồn kho
            if (product.getStockQuantity() < order.getQuantity()) {
                throw new RuntimeException(
                    String.format("Không đủ tồn kho. Tồn kho hiện tại: %d, Yêu cầu: %d",
                        product.getStockQuantity(), order.getQuantity())
                );
            }

            // Cập nhật tồn kho
            int newStock = product.getStockQuantity() - order.getQuantity();
            product.setStockQuantity(newStock);
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);

            // Cập nhật trạng thái đơn hàng
            order.setInventoryUpdated(true);
            order.setInventoryUpdatedAt(LocalDateTime.now());
            if (order.getStatus() == com.intel25.apporder.entity.OrderStatus.PROCESSING ||
                order.getStatus() == com.intel25.apporder.entity.OrderStatus.EMAIL_SENT) {
                order.setStatus(com.intel25.apporder.entity.OrderStatus.INVENTORY_UPDATED);
            }
            orderRepository.save(order);

            log.info("Đã cập nhật tồn kho cho đơn hàng ID: {}. Tồn kho mới: {}", orderId, newStock);
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật tồn kho cho đơn hàng ID: {} - {}", orderId, e.getMessage());
            throw e;
        }
    }
}

