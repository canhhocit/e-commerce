# E-Commerce REST API


## 🛠️ Tech Stack

 ### Công nghệ 
 - Java 17 + Spring Boot 3.5
 - Spring Security + JWT
 - Spring Data JPA + MySQL
 - MapStruct 
 - Lombok 
 - Swagger


---

## ✨ Tính năng chính

### 🔐 Authentication
- **Đăng ký** tài khoản mới
- **Đăng nhập** → nhận JWT token
- Phân quyền 2 role: `ADMIN` và `CUSTOMER`

### 📦 Product Management
- Xem danh sách sản phẩm có **tìm kiếm & phân trang** (public)
- Thêm / Sửa / Xóa sản phẩm (chỉ `ADMIN`)
- Xóa mềm (soft delete) – sản phẩm không bị mất khỏi database

### 🛒 Cart (Giỏ hàng)
- Mỗi user có **1 giỏ hàng riêng**
- Thêm, xem, xóa sản phẩm trong giỏ

### 🧾 Order (Đặt hàng)
- **Checkout**: chuyển giỏ hàng → đơn hàng
- Tự động kiểm tra & trừ tồn kho
- Trạng thái đơn hàng: `PENDING` → `COMPLETED` / `CANCELLED`

---

## 🚀 Hướng dẫn chạy project

### Yêu cầu
- Java 17+
- MySQL
- Maven

### Cấu hình `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mouse_shop
spring.datasource.username=root
spring.datasource.password=your_password
application.security.jwt.secret-key=your_secret_key

```

### Chạy ứng dụng
```bash
mvn spring-boot:run
```

### Xem API docs (Swagger UI)
```
http://localhost:8080/swagger-ui.html
```

---

## 📁 Cấu trúc thư mục

```
src/
├── config/          # Security, JWT, OpenAPI config
├── controller/      # REST API endpoints
├── dto/             # Request / Response objects
├── model/           # Database entities
├── exception/       # Global error handling
├── mapper/          # MapStruct mappers
├── repository/      # JPA repositories
└── service/         # Business logic
```
