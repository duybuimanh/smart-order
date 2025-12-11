# Hệ Thống Đặt Hàng & Xử Lý Nền - Intel25

Hệ thống đặt hàng với xử lý nền không đồng bộ sử dụng Spring Boot, Redis, và JPA.

## Tính Năng

### 1. Chức Năng Người Dùng (Frontend/API)

#### 1.1. Tạo Đơn Hàng
- **Endpoint**: `POST /api/orders`
- **Request Body**:
```json
{
  "customerName": "Nguyễn Văn A",
  "productId": 1,
  "quantity": 2,
  "price": 25000000.0
}
```
- **Response**: 
  - Lưu đơn hàng vào database
  - Phản hồi ngay: "Đơn hàng của bạn đã được ghi nhận. Vui lòng kiểm tra email xác nhận."
  - Gửi message vào Redis queue để xử lý nền

#### 1.2. Theo Dõi Trạng Thái Đơn Hàng
- **Endpoint**: `GET /api/orders/{id}/status`
- **Response**: Trạng thái chi tiết:
  - Đơn hàng đã lưu chưa?
  - Email đã gửi chưa?
  - Tồn kho đã cập nhật chưa?
  - Log đã ghi chưa?

### 2. Chức Năng Xử Lý Nền (Background Processing)

Sau khi đơn hàng được tạo, hệ thống tự động xử lý không đồng bộ:

1. **Gửi Email Xác Nhận**: Giả lập - log ra console
2. **Cập nhật Tồn Kho**: Trừ số lượng sản phẩm
3. **Ghi Log Chi Tiết**: Lưu vào database và file log
4. **Cập Nhật Trạng Thái**: Tự động cập nhật trạng thái đơn hàng

### 3. Dashboard Admin

- **Xem Danh Sách Đơn Hàng**: `GET /api/admin/orders?limit=10`
- **Thống Kê Đơn Hàng**: `GET /api/admin/orders/stats`

## Công Nghệ Sử Dụng

- **Spring Boot 3.2.0**
- **Spring Data JPA** - Quản lý database
- **Redis** - Message queue cho xử lý nền
- **H2 Database** - Database in-memory (development)
- **PostgreSQL** - Database production (tùy chọn)
- **Lombok** - Giảm boilerplate code
- **Maven** - Quản lý dependencies

## Cài Đặt và Chạy

### Yêu Cầu

- Java 17+
- Maven 3.6+
- Redis Server (chạy trên localhost:6379)

### Cài Đặt Redis

**Windows:**
```bash
# Tải Redis từ https://github.com/microsoftarchive/redis/releases
# Hoặc sử dụng WSL/Docker
docker run -d -p 6379:6379 redis:latest
```

**Linux/Mac:**
```bash
# Ubuntu/Debian
sudo apt-get install redis-server
sudo systemctl start redis

# Mac
brew install redis
brew services start redis
```

### Chạy Ứng Dụng

1. **Clone và vào thư mục dự án**
```bash
cd apporder
```

2. **Build project**
```bash
mvn clean install
```

3. **Chạy ứng dụng**
```bash
mvn spring-boot:run
```

4. **Truy cập ứng dụng**
- API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:orderdb`
  - Username: `sa`
  - Password: (để trống)

## API Endpoints

### Tạo Đơn Hàng
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Nguyễn Văn A",
    "productId": 1,
    "quantity": 2,
    "price": 25000000.0
  }'
```

### Xem Đơn Hàng
```bash
curl http://localhost:8080/api/orders/1
```

### Xem Trạng Thái Đơn Hàng
```bash
curl http://localhost:8080/api/orders/1/status
```

### Dashboard - Danh Sách Đơn Hàng
```bash
curl http://localhost:8080/api/admin/orders?limit=10
```

### Dashboard - Thống Kê
```bash
curl http://localhost:8080/api/admin/orders/stats
```

## Cấu Trúc Dự Án

```
apporder/
├── src/
│   ├── main/
│   │   ├── java/com/intel25/apporder/
│   │   │   ├── entity/          # Entities: Order, Product, OrderLog
│   │   │   ├── repository/      # JPA Repositories
│   │   │   ├── service/         # Business Logic
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── worker/          # Background Worker
│   │   │   ├── config/          # Configuration
│   │   │   └── dto/             # Data Transfer Objects
│   │   └── resources/
│   │       └── application.properties
│   └── test/
└── pom.xml
```

## Luồng Xử Lý

1. **Người dùng tạo đơn hàng** → API nhận request
2. **Lưu đơn hàng vào DB** → Trạng thái: CREATED
3. **Phản hồi ngay cho người dùng** → "Đơn hàng đã được ghi nhận..."
4. **Gửi message vào Redis queue** → Order ID
5. **Worker nhận message** → Bắt đầu xử lý nền
6. **Gửi email** → Trạng thái: EMAIL_SENT
7. **Cập nhật tồn kho** → Trạng thái: INVENTORY_UPDATED
8. **Ghi log** → Trạng thái: COMPLETED

## Cấu Hình

### Development (mặc định)
- Database: H2 (in-memory)
- Redis: localhost:6379

### Production
Chỉnh sửa `application-prod.properties`:
- Database: PostgreSQL
- Redis: Cấu hình theo môi trường

## Logs

- Console logs: Xem trong terminal
- File logs: `logs/order-processing.log`
- Database logs: Bảng `order_logs`

## Lưu Ý

1. **Redis phải chạy** trước khi start ứng dụng
2. Worker tự động chạy mỗi 2 giây để xử lý queue
3. Sản phẩm mẫu được tự động tạo khi khởi động lần đầu
4. Email được giả lập - chỉ log ra console

## Phát Triển Thêm

- Tích hợp email service thật (SMTP)
- Thêm authentication/authorization
- Thêm real-time dashboard với WebSocket
- Thêm retry mechanism cho failed orders
- Thêm monitoring và metrics

