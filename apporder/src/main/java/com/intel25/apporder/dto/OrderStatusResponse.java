package com.intel25.apporder.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusResponse {
    private Long orderId;
    private String status;
    private Boolean orderSaved;
    private Boolean emailSent;
    private Boolean inventoryUpdated;
    private Boolean logged;
    private LocalDateTime createdAt;
    private LocalDateTime emailSentAt;
    private LocalDateTime inventoryUpdatedAt;
    private LocalDateTime completedAt;
}

