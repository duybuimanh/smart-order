package com.intel25.apporder.controller;

import com.intel25.apporder.dto.OrderRequest;
import com.intel25.apporder.dto.OrderResponse;
import com.intel25.apporder.dto.OrderStatusResponse;
import com.intel25.apporder.entity.Order;
import com.intel25.apporder.repository.OrderRepository;
import com.intel25.apporder.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse response = orderService.getOrderById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<OrderStatusResponse> getOrderStatus(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));

        OrderStatusResponse response = new OrderStatusResponse();
        response.setOrderId(order.getId());
        response.setStatus(order.getStatus().toString());
        response.setOrderSaved(true);
        response.setEmailSent(order.getEmailSent());
        response.setInventoryUpdated(order.getInventoryUpdated());
        response.setLogged(order.getLogged());
        response.setCreatedAt(order.getCreatedAt());
        response.setEmailSentAt(order.getEmailSentAt());
        response.setInventoryUpdatedAt(order.getInventoryUpdatedAt());
        response.setCompletedAt(order.getCompletedAt());

        return ResponseEntity.ok(response);
    }
}

