package com.intel25.apporder.service;

import com.intel25.apporder.entity.Order;
import com.intel25.apporder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final OrderRepository orderRepository;

    @Transactional
    public void sendConfirmationEmail(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

            // Giả lập gửi email - log ra console
            String emailContent = String.format(
                "=== EMAIL XÁC NHẬN ĐƠN HÀNG ===\n" +
                "Kính gửi: %s\n" +
                "Đơn hàng #%d của bạn đã được xác nhận.\n" +
                "Sản phẩm ID: %d\n" +
                "Số lượng: %d\n" +
                "Giá: %.2f VNĐ\n" +
                "Tổng tiền: %.2f VNĐ\n" +
                "Thời gian: %s\n" +
                "=============================",
                order.getCustomerName(),
                order.getId(),
                order.getProductId(),
                order.getQuantity(),
                order.getPrice(),
                order.getTotalAmount(),
                LocalDateTime.now()
            );

            log.info("\n{}", emailContent);

            // Cập nhật trạng thái
            order.setEmailSent(true);
            order.setEmailSentAt(LocalDateTime.now());
            if (order.getStatus() == com.intel25.apporder.entity.OrderStatus.CREATED) {
                order.setStatus(com.intel25.apporder.entity.OrderStatus.PROCESSING);
            }
            orderRepository.save(order);

            log.info("Email xác nhận đã được gửi cho đơn hàng ID: {}", orderId);
        } catch (Exception e) {
            log.error("Lỗi khi gửi email cho đơn hàng ID: {} - {}", orderId, e.getMessage());
            throw e;
        }
    }
}

