package com.intel25.apporder.worker;

import com.intel25.apporder.entity.Order;
import com.intel25.apporder.entity.OrderStatus;
import com.intel25.apporder.repository.OrderRepository;
import com.intel25.apporder.service.EmailService;
import com.intel25.apporder.service.InventoryService;
import com.intel25.apporder.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderProcessingWorker {
    private final RedisTemplate<String, Object> redisTemplate;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final InventoryService inventoryService;
    private final LogService logService;

    @Value("${app.order.queue.name:order:queue}")
    private String queueName;

    @Scheduled(fixedDelay = 2000) // Chạy mỗi 2 giây
    public void processOrderQueue() {
        try {
            // Lấy message từ queue (blocking pop từ bên trái)
            Object orderIdObj = redisTemplate.opsForList().leftPop(queueName);
            
            if (orderIdObj != null) {
                Long orderId = Long.parseLong(orderIdObj.toString());
                log.info("Worker nhận được đơn hàng để xử lý: Order ID = {}", orderId);
                
                processOrder(orderId);
            }
        } catch (Exception e) {
            log.error("Lỗi trong worker xử lý đơn hàng: {}", e.getMessage(), e);
        }
    }

    private void processOrder(Long orderId) {
        Order order = null;
        try {
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + orderId));

            // Cập nhật trạng thái đang xử lý
            order.setStatus(OrderStatus.PROCESSING);
            orderRepository.save(order);

            // 1. Gửi email xác nhận
            log.info("Bắt đầu gửi email cho đơn hàng ID: {}", orderId);
            emailService.sendConfirmationEmail(orderId);
            order.setStatus(OrderStatus.EMAIL_SENT);
            orderRepository.save(order);

            // 2. Cập nhật tồn kho
            log.info("Bắt đầu cập nhật tồn kho cho đơn hàng ID: {}", orderId);
            inventoryService.updateInventory(orderId);

            // 3. Ghi log chi tiết
            log.info("Bắt đầu ghi log cho đơn hàng ID: {}", orderId);
            logService.logOrderDetails(orderId);

            // 4. Hoàn thành đơn hàng
            order = orderRepository.findById(orderId).orElse(order);
            order.setStatus(OrderStatus.COMPLETED);
            order.setCompletedAt(LocalDateTime.now());
            orderRepository.save(order);

            log.info("Đã hoàn thành xử lý đơn hàng ID: {}", orderId);
        } catch (Exception e) {
            log.error("Lỗi khi xử lý đơn hàng ID: {} - {}", orderId, e.getMessage(), e);
            
            // Cập nhật trạng thái thất bại
            if (order != null) {
                try {
                    order.setStatus(OrderStatus.FAILED);
                    orderRepository.save(order);
                } catch (Exception ex) {
                    log.error("Không thể cập nhật trạng thái thất bại: {}", ex.getMessage());
                }
            }
        }
    }
}

