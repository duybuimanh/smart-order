#!/bin/bash

# Script test API cho hệ thống đặt hàng
BASE_URL="http://localhost:8080/api"

echo "=== TEST HỆ THỐNG ĐẶT HÀNG ==="
echo ""

# 1. Tạo đơn hàng
echo "1. Tạo đơn hàng mới..."
ORDER_RESPONSE=$(curl -s -X POST "$BASE_URL/orders" \
  -H "Content-Type: application/json" \
  -d '{
    "customerName": "Nguyễn Văn A",
    "productId": 1,
    "quantity": 2,
    "price": 25000000.0
  }')

echo "$ORDER_RESPONSE" | jq .
ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.id')
echo "Order ID: $ORDER_ID"
echo ""

# Đợi 5 giây để worker xử lý
echo "Đợi 5 giây để worker xử lý đơn hàng..."
sleep 5
echo ""

# 2. Xem trạng thái đơn hàng
echo "2. Xem trạng thái đơn hàng..."
curl -s "$BASE_URL/orders/$ORDER_ID/status" | jq .
echo ""

# 3. Xem chi tiết đơn hàng
echo "3. Xem chi tiết đơn hàng..."
curl -s "$BASE_URL/orders/$ORDER_ID" | jq .
echo ""

# 4. Dashboard - Danh sách đơn hàng
echo "4. Dashboard - Danh sách đơn hàng (10 mới nhất)..."
curl -s "$BASE_URL/admin/orders?limit=10" | jq .
echo ""

# 5. Dashboard - Thống kê
echo "5. Dashboard - Thống kê đơn hàng..."
curl -s "$BASE_URL/admin/orders/stats" | jq .
echo ""

echo "=== HOÀN THÀNH TEST ==="

