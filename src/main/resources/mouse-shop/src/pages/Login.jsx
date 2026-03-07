import { useState } from "react";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";

const API_URL = "http://localhost:8080/mouse-shop/auth/login";

export default function Login() {
  const { login } = useAuth();
  const [form, setForm] = useState({ username: "", password: "" });
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const validate = () => {
    const errs = {};
    if (!form.username) errs.username = "Vui lòng nhập tên đăng nhập";
    if (!form.password) errs.password = "Vui lòng nhập mật khẩu";
    return errs;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
    if (errors[name]) setErrors((prev) => ({ ...prev, [name]: "" }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError("");

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
        login(data.result.token); // Lưu token + cập nhật user context
        // Chuyển hướng — thay bằng navigate("/") nếu dùng React Router
        // Giải mã token để lấy role
        const payload = JSON.parse(atob(data.result.token.split(".")[1]));

        // if (payload.scope === "ADMIN") {
        //   window.location.href = "/admin";
        // } else {
        //   window.location.href = "/";
        // }
        alert("Đăng nhập thành công")
         navigate(payload.scope === "ADMIN" ? "/admin" : "/");
      } else {
        setServerError(data.message || "Sai tên đăng nhập hoặc mật khẩu");
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
        <div style={styles.header}>
          <div style={styles.logo}>🛍️</div>
          <h1 style={styles.title}>Đăng nhập</h1>
          <p style={styles.subtitle}>Chào mừng bạn quay trở lại!</p>
        </div>

        {serverError && <div style={styles.alertError}>{serverError}</div>}

        <form onSubmit={handleSubmit} noValidate>
          <div style={styles.fieldWrap}>
            <label style={styles.label}>Tên đăng nhập</label>
            <input
              name="username"
              placeholder="Nhập tên đăng nhập"
              value={form.username}
              onChange={handleChange}
              style={{
                ...styles.input,
                ...(errors.username ? styles.inputError : {}),
              }}
            />
            {errors.username && (
              <span style={styles.errorText}>{errors.username}</span>
            )}
          </div>

          <div style={styles.fieldWrap}>
            <label style={styles.label}>Mật khẩu</label>
            <input
              type="password"
              name="password"
              placeholder="Nhập mật khẩu"
              value={form.password}
              onChange={handleChange}
              style={{
                ...styles.input,
                ...(errors.password ? styles.inputError : {}),
              }}
            />
            {errors.password && (
              <span style={styles.errorText}>{errors.password}</span>
            )}
          </div>

          <button type="submit" style={styles.btn} disabled={loading}>
            {loading ? "Đang đăng nhập..." : "Đăng nhập"}
          </button>
        </form>

        <p style={styles.registerText}>
          Chưa có tài khoản?{" "}
          <a href="/register" style={styles.link}>
            Đăng ký ngay
          </a>
        </p>
      </div>
    </div>
  );
}
// css
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
    maxWidth: "420px",
    boxShadow: "0 20px 60px rgba(0,0,0,0.2)",
  },
  header: { textAlign: "center", marginBottom: "28px" },
  logo: { fontSize: "40px", marginBottom: "8px" },
  title: {
    margin: "0 0 6px",
    fontSize: "26px",
    fontWeight: "700",
    color: "#1a1a2e",
  },
  subtitle: { margin: 0, color: "#888", fontSize: "14px" },
  fieldWrap: { display: "flex", flexDirection: "column", marginBottom: "16px" },
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
    color: "#333",
  },
  inputError: { border: "1.5px solid #e74c3c" },
  errorText: { color: "#e74c3c", fontSize: "12px", marginTop: "4px" },
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
  registerText: {
    textAlign: "center",
    marginTop: "20px",
    fontSize: "14px",
    color: "#666",
  },
  link: { color: "#667eea", fontWeight: "600", textDecoration: "none" },
};
