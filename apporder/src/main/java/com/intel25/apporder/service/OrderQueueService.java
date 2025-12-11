package com.intel25.apporder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderQueueService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.order.queue.name:order:queue}")
    private String queueName;

    public void sendOrderCreatedMessage(Long orderId) {
        try {
            // Gửi orderId vào Redis list (queue)
            redisTemplate.opsForList().rightPush(queueName, orderId.toString());
            log.info("Đã gửi message vào queue: Order ID = {}", orderId);
        } catch (Exception e) {
            log.error("Lỗi khi gửi message vào queue cho đơn hàng ID: {} - {}", orderId, e.getMessage());
            throw new RuntimeException("Không thể gửi message vào queue", e);
        }
    }
}

