# Hướng Dẫn Nhanh

## Bước 1: Khởi động Redis

**Windows:**
```bash
# Sử dụng Docker
docker run -d -p 6379:6379 redis:latest

# Hoặc tải Redis từ: https://github.com/microsoftarchive/redis/releases
```

**Linux/Mac:**
```bash
# Ubuntu/Debian
sudo systemctl start redis

# Mac
brew services start redis
```

## Bước 2: Chạy Ứng Dụng

```bash
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: http://localhost:8080

## Bước 3: Test API

### Tạo đơn hàng
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d "{\"customerName\": \"Nguyễn Văn A\", \"productId\": 1, \"quantity\": 2, \"price\": 25000000.0}"
```

**Response:**
```json
{
  "id": 1,
  "customerName": "Nguyễn Văn A",
  "productId": 1,
  "quantity": 2,
  "price": 25000000.0,
  "totalAmount": 50000000.0,
  "status": "CREATED",
  "message": "Đơn hàng của bạn đã được ghi nhận. Vui lòng kiểm tra email xác nhận."
}
```

### Xem trạng thái đơn hàng (sau 5-10 giây)
```bash
curl http://localhost:8080/api/orders/1/status
```

**Response:**
```json
{
  "orderId": 1,
  "status": "COMPLETED",
  "orderSaved": true,
  "emailSent": true,
  "inventoryUpdated": true,
  "logged": true,
  "createdAt": "2024-01-01T10:00:00",
  "emailSentAt": "2024-01-01T10:00:02",
  "inventoryUpdatedAt": "2024-01-01T10:00:03",
  "completedAt": "2024-01-01T10:00:04"
}
```

### Dashboard - Xem danh sách đơn hàng
```bash
curl http://localhost:8080/api/admin/orders?limit=10
```

### Dashboard - Thống kê
```bash
curl http://localhost:8080/api/admin/orders/stats
```

## Kiểm Tra Logs

- **Console**: Xem trong terminal nơi chạy ứng dụng
- **File**: `logs/order-processing.log`
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: (để trống)

## Sản Phẩm Mẫu

Khi khởi động lần đầu, hệ thống tự động tạo 3 sản phẩm:
- ID 1: Laptop Dell XPS 15 (50 tồn kho)
- ID 2: iPhone 15 Pro (30 tồn kho)
- ID 3: Samsung Galaxy S24 (40 tồn kho)

## Lưu Ý

1. **Redis phải chạy** trước khi start ứng dụng
2. Worker tự động xử lý queue mỗi 2 giây
3. Đợi 5-10 giây sau khi tạo đơn để xem trạng thái đầy đủ

