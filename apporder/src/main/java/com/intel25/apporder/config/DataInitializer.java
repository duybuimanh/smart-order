package com.intel25.apporder.config;

import com.intel25.apporder.entity.Product;
import com.intel25.apporder.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    private final ProductRepository productRepository;

    @Override
    public void run(String... args) {
        // Khởi tạo một số sản phẩm mẫu
        if (productRepository.count() == 0) {
            Product product1 = new Product();
            product1.setName("Laptop Dell XPS 15");
            product1.setDescription("Laptop cao cấp với màn hình 15 inch");
            product1.setPrice(25000000.0);
            product1.setStockQuantity(50);
            productRepository.save(product1);

            Product product2 = new Product();
            product2.setName("iPhone 15 Pro");
            product2.setDescription("Điện thoại thông minh Apple");
            product2.setPrice(30000000.0);
            product2.setStockQuantity(30);
            productRepository.save(product2);

            Product product3 = new Product();
            product3.setName("Samsung Galaxy S24");
            product3.setDescription("Điện thoại thông minh Samsung");
            product3.setPrice(20000000.0);
            product3.setStockQuantity(40);
            productRepository.save(product3);

            log.info("Đã khởi tạo {} sản phẩm mẫu", productRepository.count());
        }
    }
}

