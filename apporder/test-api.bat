@echo off
REM Script test API cho hệ thống đặt hàng (Windows)

set BASE_URL=http://localhost:8080/api

echo === TEST HỆ THỐNG ĐẶT HÀNG ===
echo.

REM 1. Tạo đơn hàng
echo 1. Tạo đơn hàng mới...
curl -X POST "%BASE_URL%/orders" ^
  -H "Content-Type: application/json" ^
  -d "{\"customerName\": \"Nguyễn Văn A\", \"productId\": 1, \"quantity\": 2, \"price\": 25000000.0}"

echo.
echo Đợi 5 giây để worker xử lý đơn hàng...
timeout /t 5 /nobreak >nul
echo.

REM 2. Xem trạng thái đơn hàng (thay {id} bằng ID thực tế)
echo 2. Xem trạng thái đơn hàng...
echo Vui lòng thay {id} bằng Order ID từ bước 1
curl "%BASE_URL%/orders/1/status"

echo.
echo === HOÀN THÀNH TEST ===
pause

