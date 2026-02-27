📧 Simple Email Verification Flow (Beginner Version)
🎯 Mục tiêu

Triển khai xác thực email đơn giản cho người mới:

Register → Gửi email → Click link → Kích hoạt tài khoản → Login

Không dùng bảng token riêng.
Không dùng Redis.
Không xử lý expiry phức tạp.

🗄️ Database Design (Đơn giản nhất)

Chỉ cần chỉnh sửa bảng users.

🧱 users table
Field	Type	Description
id	BIGINT	Primary key
username	VARCHAR	Unique
email	VARCHAR	Unique
password	VARCHAR	Encoded password
role	VARCHAR	USER / ADMIN
enabled	BOOLEAN	Mặc định = false
verification_token	VARCHAR	Lưu UUID
🔁 Flow Hoạt Động
1️⃣ Register
Endpoint
POST /api/auth/register
Logic
1. Validate username/email chưa tồn tại
2. Encode password
3. Set enabled = false
4. Generate UUID token
5. Lưu token vào verification_token
6. Save user
7. Gửi email chứa link verify
8. Trả message: "Please check your email"
2️⃣ Link Gửi Trong Email
http://localhost:8080/api/auth/verify?token=abc123
3️⃣ Verify Email
Endpoint
GET /api/auth/verify?token=abc123
Logic
1. Tìm user theo verification_token
2. Nếu không tồn tại → báo lỗi
3. Set enabled = true
4. Set verification_token = null
5. Save user
6. Trả message: "Email verified successfully"
🔐 Chặn Login Khi Chưa Verify

Trong UserDetailsService:

if (!user.isEnabled()) {
    throw new DisabledException("Please verify your email");
}
🧠 Sequence Flow (Tóm Tắt)
User → POST /register
Server → Save user (enabled=false, token=UUID)
Server → Send email

User → Click link
User → GET /verify?token=...
Server → Find user by token
Server → Enable account
Server → Remove token
Server → Success