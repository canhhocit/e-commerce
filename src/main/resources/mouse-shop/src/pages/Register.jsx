import { useState } from "react";

const API_URL = "http://localhost:8080/mouse-shop/auth/register";

export default function Register() {
  const [form, setForm] = useState({
    username: "",
    password: "",
    email: "",
    fullName: "",
    phone: "",
    address: "",
  });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState("");
  const [serverError, setServerError] = useState("");

  // Validate từng field
  const validate = () => {
    const errs = {};
    if (!form.username || form.username.length < 3)
      errs.username = "Tên đăng nhập tối thiểu 3 ký tự";
    if (!form.password || form.password.length < 6)
      errs.password = "Mật khẩu tối thiểu 6 ký tự";
    if (!form.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email))
      errs.email = "Email không hợp lệ";
    if (!form.fullName) errs.fullName = "Vui lòng nhập họ tên";
    if (form.phone && !/^[0-9]{10,11}$/.test(form.phone))
      errs.phone = "Số điện thoại 10-11 chữ số";
    return errs;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    // Xoá lỗi khi người dùng sửa
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError("");
    setSuccess("");

    const errs = validate();
    if (Object.keys(errs).length > 0) {
      setErrors(errs);
      return;
    }

    setLoading(true);
    try {
      const res = await fetch(API_URL, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await res.json();

      if (res.ok && data.code === 1000) {
        setSuccess(data.result || "Đăng ký thành công! Kiểm tra email để xác thực.");
        setForm({ username: "", password: "", email: "", fullName: "", phone: "", address: "" });
      } else {
        setServerError(data.message || "Đăng ký thất bại.");
      }
    } catch {
      setServerError("Không thể kết nối đến server.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        {/* Header */}
        <div style={styles.header}>
          <div style={styles.logo}>🛍️</div>
          <h1 style={styles.title}>Tạo tài khoản</h1>
          <p style={styles.subtitle}>Đăng ký để bắt đầu mua sắm</p>
        </div>

        {/* Success / Error banner */}
        {success && <div style={styles.alertSuccess}>{success}</div>}
        {serverError && <div style={styles.alertError}>{serverError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          {/* Row 1: username + fullName */}
          <div style={styles.row}>
            <Field
              label="Tên đăng nhập *"
              name="username"
              placeholder="Ít nhất 3 ký tự"
              value={form.username}
              onChange={handleChange}
              error={errors.username}
            />
            <Field
              label="Họ và tên *"
              name="fullName"
              placeholder="Nguyễn Văn A"
              value={form.fullName}
              onChange={handleChange}
              error={errors.fullName}
            />
          </div>

          {/* Row 2: email + password */}
          <div style={styles.row}>
            <Field
              label="Email *"
              name="email"
              type="email"
              placeholder="example@email.com"
              value={form.email}
              onChange={handleChange}
              error={errors.email}
            />
            <Field
              label="Mật khẩu *"
              name="password"
              type="password"
              placeholder="Ít nhất 6 ký tự"
              value={form.password}
              onChange={handleChange}
              error={errors.password}
            />
          </div>

          {/* Row 3: phone + address */}
          <div style={styles.row}>
            <Field
              label="Số điện thoại"
              name="phone"
              placeholder="0xxxxxxxxx"
              value={form.phone}
              onChange={handleChange}
              error={errors.phone}
            />
            <Field
              label="Địa chỉ"
              name="address"
              placeholder="Địa chỉ giao hàng"
              value={form.address}
              onChange={handleChange}
              error={errors.address}
            />
          </div>

          <button type="submit" style={styles.btn} disabled={loading}>
            {loading ? "Đang xử lý..." : "Đăng ký ngay"}
          </button>
        </form>

        <p style={styles.loginText}>
          Đã có tài khoản?{" "}
          <a href="/login" style={styles.link}>Đăng nhập</a>
        </p>
      </div>
    </div>
  );
}

// Component Field tái sử dụng
function Field({ label, name, type = "text", placeholder, value, onChange, error }) {
  return (
    <div style={styles.fieldWrap}>
      <label style={styles.label}>{label}</label>
      <input
        type={type}
        name={name}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        style={{ ...styles.input, ...(error ? styles.inputError : {}) }}
      />
      {error && <span style={styles.errorText}>{error}</span>}
    </div>
  );
}

// ---- Styles ----
const styles = {
  page: {
    minHeight: "100vh",
    background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    padding: "24px",
    fontFamily: "'Segoe UI', sans-serif",
  },
  card: {
    background: "#fff",
    borderRadius: "20px",
    padding: "40px",
    width: "100%",
    maxWidth: "680px",
    boxShadow: "0 20px 60px rgba(0,0,0,0.2)",
  },
  header: {
    textAlign: "center",
    marginBottom: "28px",
  },
  logo: {
    fontSize: "40px",
    marginBottom: "8px",
  },
  title: {
    margin: "0 0 6px",
    fontSize: "26px",
    fontWeight: "700",
    color: "#1a1a2e",
  },
  subtitle: {
    margin: 0,
    color: "#888",
    fontSize: "14px",
  },
  row: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "16px",
    marginBottom: "4px",
  },
  fieldWrap: {
    display: "flex",
    flexDirection: "column",
    marginBottom: "16px",
  },
  label: {
    fontSize: "13px",
    fontWeight: "600",
    color: "#444",
    marginBottom: "6px",
  },
  input: {
    padding: "11px 14px",
    borderRadius: "10px",
    border: "1.5px solid #e0e0e0",
    fontSize: "14px",
    outline: "none",
    transition: "border 0.2s",
    color: "#333",
  },
  inputError: {
    border: "1.5px solid #e74c3c",
  },
  errorText: {
    color: "#e74c3c",
    fontSize: "12px",
    marginTop: "4px",
  },
  btn: {
    width: "100%",
    padding: "14px",
    background: "linear-gradient(135deg, #667eea, #764ba2)",
    color: "#fff",
    border: "none",
    borderRadius: "12px",
    fontSize: "15px",
    fontWeight: "700",
    cursor: "pointer",
    marginTop: "8px",
    transition: "opacity 0.2s",
  },
  alertSuccess: {
    background: "#d4edda",
    color: "#155724",
    border: "1px solid #c3e6cb",
    borderRadius: "10px",
    padding: "12px 16px",
    marginBottom: "20px",
    fontSize: "14px",
  },
  alertError: {
    background: "#f8d7da",
    color: "#721c24",
    border: "1px solid #f5c6cb",
    borderRadius: "10px",
    padding: "12px 16px",
    marginBottom: "20px",
    fontSize: "14px",
  },
  loginText: {
    textAlign: "center",
    marginTop: "20px",
    fontSize: "14px",
    color: "#666",
  },
  link: {
    color: "#667eea",
    fontWeight: "600",
    textDecoration: "none",
  },
};