package com.intel25.apporder.entity;

public enum OrderStatus {
    CREATED,           // Đơn hàng đã được tạo
    PROCESSING,        // Đang xử lý
    EMAIL_SENT,        // Email đã gửi
    INVENTORY_UPDATED, // Tồn kho đã cập nhật
    COMPLETED,         // Hoàn thành
    FAILED             // Thất bại
}

