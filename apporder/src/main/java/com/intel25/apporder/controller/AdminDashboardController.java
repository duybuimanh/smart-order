package com.intel25.apporder.controller;

import com.intel25.apporder.dto.OrderResponse;
import com.intel25.apporder.entity.Order;
import com.intel25.apporder.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final OrderRepository orderRepository;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "10") int limit) {
        List<Order> orders = orderRepository.findAllOrderByCreatedAtDesc();
        
        // Giới hạn số lượng
        if (limit > 0 && limit < orders.size()) {
            orders = orders.subList(0, limit);
        }

        List<OrderResponse> responses = orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/orders/stats")
    public ResponseEntity<OrderStatsResponse> getOrderStats() {
        List<Order> allOrders = orderRepository.findAll();
        
        long totalOrders = allOrders.size();
        long completedOrders = allOrders.stream()
                .filter(o -> o.getStatus() == com.intel25.apporder.entity.OrderStatus.COMPLETED)
                .count();
        long processingOrders = allOrders.stream()
                .filter(o -> o.getStatus() == com.intel25.apporder.entity.OrderStatus.PROCESSING ||
                           o.getStatus() == com.intel25.apporder.entity.OrderStatus.EMAIL_SENT ||
                           o.getStatus() == com.intel25.apporder.entity.OrderStatus.INVENTORY_UPDATED)
                .count();
        long failedOrders = allOrders.stream()
                .filter(o -> o.getStatus() == com.intel25.apporder.entity.OrderStatus.FAILED)
                .count();

        OrderStatsResponse stats = new OrderStatsResponse();
        stats.setTotalOrders(totalOrders);
        stats.setCompletedOrders(completedOrders);
        stats.setProcessingOrders(processingOrders);
        stats.setFailedOrders(failedOrders);

        return ResponseEntity.ok(stats);
    }

    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setProductId(order.getProductId());
        response.setQuantity(order.getQuantity());
        response.setPrice(order.getPrice());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setEmailSent(order.getEmailSent());
        response.setInventoryUpdated(order.getInventoryUpdated());
        response.setLogged(order.getLogged());
        return response;
    }

    // Inner class for stats response
    public static class OrderStatsResponse {
        private long totalOrders;
        private long completedOrders;
        private long processingOrders;
        private long failedOrders;

        // Getters and setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }
        public long getProcessingOrders() { return processingOrders; }
        public void setProcessingOrders(long processingOrders) { this.processingOrders = processingOrders; }
        public long getFailedOrders() { return failedOrders; }
        public void setFailedOrders(long failedOrders) { this.failedOrders = failedOrders; }
    }
}

