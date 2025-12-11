package com.intel25.apporder.dto;

import com.intel25.apporder.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private String customerName;
    private Long productId;
    private Integer quantity;
    private Double price;
    private Double totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private Boolean emailSent;
    private Boolean inventoryUpdated;
    private Boolean logged;
    private String message;
}

