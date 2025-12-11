package com.intel25.apporder.service;

import com.intel25.apporder.dto.OrderRequest;
import com.intel25.apporder.dto.OrderResponse;
import com.intel25.apporder.entity.Order;
import com.intel25.apporder.entity.OrderStatus;
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
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderQueueService orderQueueService;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        // Kiểm tra sản phẩm có tồn tại không
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại với ID: " + request.getProductId()));

        // Tính tổng tiền
        Double totalAmount = request.getPrice() * request.getQuantity();

        // Tạo đơn hàng
        Order order = new Order();
        order.setCustomerName(request.getCustomerName());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setEmailSent(false);
        order.setInventoryUpdated(false);
        order.setLogged(false);

        // Lưu vào database
        order = orderRepository.save(order);
        log.info("Đơn hàng đã được tạo: Order ID = {}", order.getId());

        // Gửi message vào queue để xử lý nền
        orderQueueService.sendOrderCreatedMessage(order.getId());

        // Trả về response
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
        response.setMessage("Đơn hàng của bạn đã được ghi nhận. Vui lòng kiểm tra email xác nhận.");

        return response;
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng với ID: " + id));

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
}

