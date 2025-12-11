package com.intel25.apporder.service;

import com.intel25.apporder.entity.Order;
import com.intel25.apporder.entity.OrderLog;
import com.intel25.apporder.entity.LogStatus;
import com.intel25.apporder.repository.OrderRepository;
import com.intel25.apporder.repository.OrderLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {
    private final OrderRepository orderRepository;
    private final OrderLogRepository orderLogRepository;

    @Transactional
    public void logOrderDetails(Long orderId) {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

            // Tạo log chi tiết
            String logDetails = String.format(
                "Đơn hàng #%d - Khách hàng: %s, Sản phẩm ID: %d, Số lượng: %d, " +
                "Giá: %.2f, Tổng tiền: %.2f, Trạng thái: %s",
                order.getId(),
                order.getCustomerName(),
                order.getProductId(),
                order.getQuantity(),
                order.getPrice(),
                order.getTotalAmount(),
                order.getStatus()
            );

            // Lưu vào database
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderId(orderId);
            orderLog.setAction("ORDER_LOGGED");
            orderLog.setDetails(logDetails);
            orderLog.setTimestamp(LocalDateTime.now());
            orderLog.setStatus(LogStatus.SUCCESS);
            orderLogRepository.save(orderLog);

            // Log ra console
            log.info("=== LOG ĐƠN HÀNG ===\n{}\n===================", logDetails);

            // Cập nhật trạng thái đơn hàng
            order.setLogged(true);
            orderRepository.save(order);

            log.info("Đã ghi log chi tiết cho đơn hàng ID: {}", orderId);
        } catch (Exception e) {
            log.error("Lỗi khi ghi log cho đơn hàng ID: {} - {}", orderId, e.getMessage());
            
            // Lưu log lỗi
            try {
                OrderLog errorLog = new OrderLog();
                errorLog.setOrderId(orderId);
                errorLog.setAction("ORDER_LOGGED");
                errorLog.setDetails("Lỗi khi ghi log: " + e.getMessage());
                errorLog.setTimestamp(LocalDateTime.now());
                errorLog.setStatus(LogStatus.FAILED);
                errorLog.setErrorMessage(e.getMessage());
                orderLogRepository.save(errorLog);
            } catch (Exception ex) {
                log.error("Không thể lưu log lỗi: {}", ex.getMessage());
            }
        }
    }
}

