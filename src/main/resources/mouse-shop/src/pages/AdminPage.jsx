import { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const BASE = "http://localhost:8080/mouse-shop";

async function apiFetch(path, options = {}) {
  const token = localStorage.getItem("token");
  const res = await fetch(`${BASE}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
      ...options.headers,
    },
  });
  return res.json();
}

export default function AdminPage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const [tab, setTab] = useState("users");

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  // Debug — mở console xem scope là gì rồi báo mình
  console.log("user:", user);

  // Chấp nhận cả "ADMIN" và "ROLE_ADMIN"
  const isAdmin =
    user && (user.scope === "ADMIN" || user.scope === "ROLE_ADMIN");

  if (!isAdmin) {
    return (
      <div style={s.denied}>
        <h2>⛔ Không có quyền truy cập</h2>
        <p>Trang này chỉ dành cho Admin.</p>
        <button
          onClick={() => navigate("/login")}
          style={{ ...s.btnPrimary, marginTop: 16 }}
        >
          Về trang đăng nhập
        </button>
      </div>
    );
  }

  return (
    <div style={s.layout}>
      <aside style={s.sidebar}>
        <div style={s.brand}>🖱️ Mouse Shop</div>
        <p style={s.brandSub}>Admin Panel</p>
        <nav style={s.nav}>
          {[
            { key: "users", icon: "👤", label: "Người dùng" },
            { key: "products", icon: "📦", label: "Sản phẩm" },
            { key: "categories", icon: "🗂️", label: "Danh mục" },
          ].map((item) => (
            <button
              key={item.key}
              onClick={() => setTab(item.key)}
              style={{
                ...s.navBtn,
                ...(tab === item.key ? s.navBtnActive : {}),
              }}
            >
              <span>{item.icon}</span> {item.label}
            </button>
          ))}
        </nav>
        <button onClick={handleLogout} style={s.logoutBtn}>
          🚪 Đăng xuất
        </button>
      </aside>

      <main style={s.main}>
        <div style={s.topBar}>
          <h1 style={s.pageTitle}>
            {tab === "users" && "👤 Quản lý người dùng"}
            {tab === "products" && "📦 Quản lý sản phẩm"}
            {tab === "categories" && "🗂️ Quản lý danh mục"}
          </h1>
          <span style={s.adminBadge}>👑 {user.sub}</span>
        </div>
        {tab === "users" && <UsersTab />}
        {tab === "products" && <ProductsTab />}
        {tab === "categories" && <CategoriesTab />}
      </main>
    </div>
  );
}

function UsersTab() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    const data = await apiFetch("/users");
    setUsers(data.result || []);
    setLoading(false);
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleDelete = async (username) => {
    if (!confirm(`Xóa user "${username}"?`)) return;
    await apiFetch(`/users/${username}`, { method: "DELETE" });
    setMsg(`Đã xóa ${username}`);
    load();
  };

  return (
    <div>
      {msg && <div style={s.toast}>{msg}</div>}
      {loading ? (
        <p style={s.loading}>Đang tải...</p>
      ) : (
        <table style={s.table}>
          <thead>
            <tr>
              {["ID", "Username", "Email", "Họ tên", "SĐT", "Vai trò", ""].map(
                (h) => (
                  <th key={h} style={s.th}>
                    {h}
                  </th>
                ),
              )}
            </tr>
          </thead>
          <tbody>
            {users.map((u) => (
              <tr key={u.id} style={s.tr}>
                <td style={s.td}>{u.id}</td>
                <td style={s.td}>
                  <b>{u.username}</b>
                </td>
                <td style={s.td}>{u.email}</td>
                <td style={s.td}>{u.fullName}</td>
                <td style={s.td}>{u.phone || "—"}</td>
                <td style={s.td}>
                  <span
                    style={{
                      ...s.badge,
                      background: u.role === "ADMIN" ? "#ffd700" : "#e0f0ff",
                      color: u.role === "ADMIN" ? "#333" : "#0066cc",
                    }}
                  >
                    {u.role}
                  </span>
                </td>
                <td style={s.td}>
                  <button
                    onClick={() => handleDelete(u.username)}
                    style={s.btnDanger}
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

function ProductsTab() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [msg, setMsg] = useState("");
  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    stock: "",
    categoryId: "",
  });
  const [imageFile, setImageFile] = useState(null);

  const load = useCallback(async () => {
    setLoading(true);
    const [pData, cData] = await Promise.all([
      apiFetch("/products"),
      apiFetch("/categories"),
    ]);
    console.log("products raw:", pData); // ← thêm dòng này
    console.log("categories raw:", cData);
    setProducts(pData.result?.content || []);
    setCategories(Array.isArray(cData.result) ? cData.result : []);
    setLoading(false);
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleDelete = async (id, name) => {
    if (!confirm(`Xóa sản phẩm "${name}"?`)) return;
    await apiFetch(`/products/${id}`, { method: "DELETE" });
    setMsg(`Đã xóa "${name}"`);
    load();
  };

  const handleAdd = async () => {
    const fd = new FormData();
    fd.append(
      "product",
      new Blob(
        [
          JSON.stringify({
            name: form.name,
            description: form.description,
            price: parseFloat(form.price),
            stock: parseInt(form.stock),
            categoryId: parseInt(form.categoryId),
          }),
        ],
        { type: "application/json" },
      ),
    );
    if (imageFile) fd.append("image", imageFile);
    const token = localStorage.getItem("token");
    const res = await fetch(`${BASE}/products`, {
      method: "POST",
      headers: { Authorization: `Bearer ${token}` },
      body: fd,
    });
    const data = await res.json();
    if (data.code === 1000) {
      setMsg("Thêm sản phẩm thành công!");
      setShowForm(false);
      setForm({
        name: "",
        description: "",
        price: "",
        stock: "",
        categoryId: "",
      });
      setImageFile(null);
      load();
    } else {
      setMsg("Lỗi: " + data.message);
    }
  };

  return (
    <div>
      {msg && <div style={s.toast}>{msg}</div>}
      <button onClick={() => setShowForm(!showForm)} style={s.btnPrimary}>
        {showForm ? "✕ Đóng" : "+ Thêm sản phẩm"}
      </button>
      {showForm && (
        <div style={s.formBox}>
          <h3 style={{ marginBottom: 16 }}>Thêm sản phẩm mới</h3>
          <div style={s.formGrid}>
            <FormField
              label="Tên sản phẩm"
              value={form.name}
              onChange={(v) => setForm((f) => ({ ...f, name: v }))}
            />
            <FormField
              label="Giá (VNĐ)"
              type="number"
              value={form.price}
              onChange={(v) => setForm((f) => ({ ...f, price: v }))}
            />
            <FormField
              label="Số lượng"
              type="number"
              value={form.stock}
              onChange={(v) => setForm((f) => ({ ...f, stock: v }))}
            />
            <div style={s.fieldWrap}>
              <label style={s.label}>Danh mục</label>
              <select
                value={form.categoryId}
                onChange={(e) =>
                  setForm((f) => ({ ...f, categoryId: e.target.value }))
                }
                style={s.input}
              >
                <option value="">-- Chọn danh mục --</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </div>
          </div>
          <FormField
            label="Mô tả"
            value={form.description}
            onChange={(v) => setForm((f) => ({ ...f, description: v }))}
          />
          <div style={s.fieldWrap}>
            <label style={s.label}>Hình ảnh</label>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setImageFile(e.target.files[0])}
              style={s.input}
            />
          </div>
          <button onClick={handleAdd} style={{ ...s.btnPrimary, marginTop: 8 }}>
            💾 Lưu sản phẩm
          </button>
        </div>
      )}
      {loading ? (
        <p style={s.loading}>Đang tải...</p>
      ) : (
        <table style={s.table}>
          <thead>
            <tr>
              {[
                "Ảnh",
                "Tên",
                "Giá",
                "Tồn kho",
                "Danh mục",
                "Trạng thái",
                "",
              ].map((h) => (
                <th key={h} style={s.th}>
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {products.map((p) => (
              <tr key={p.id} style={s.tr}>
                {/* <td style={s.td}>{p.id}</td> */}
                <td style={s.td}>
                  {p.imageUrl ? (
                    <img
                     src={`http://localhost:8080/mouse-shop${p.imageUrl}`}
                      style={{
                        width: 56,
                        height: 56,
                        objectFit: "cover",
                        borderRadius: 8,
                      }}
                    />
                  ) : (
                    <div
                      style={{
                        width: 56,
                        height: 56,
                        borderRadius: 8,
                        background: "#f0f0f0",
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        fontSize: 20,
                      }}
                    >
                      📦
                    </div>
                  )}
                </td>
                <td style={s.td}>
                  <b>{p.name}</b>
                </td>
                <td style={s.td}>{p.price?.toLocaleString("vi-VN")}₫</td>
                <td style={s.td}>{p.stock}</td>
                <td style={s.td}>
                  {categories.find((c) => c.id === Number(p.categoryId))?.name || "—"}
                </td>
                <td style={s.td}>
                  <span
                    style={{
                      ...s.badge,
                      background: p.active ? "#d4edda" : "#f8d7da",
                      color: p.active ? "#155724" : "#721c24",
                    }}
                  >
                    {p.active ? "Đang bán" : "Ẩn"}
                  </span>
                </td>
                <td style={s.td}>
                  <button
                    onClick={() => handleDelete(p.id, p.name)}
                    style={s.btnDanger}
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

function CategoriesTab() {
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: "", description: "" });
  const [msg, setMsg] = useState("");

  const load = useCallback(async () => {
    setLoading(true);
    const data = await apiFetch("/categories");
    setCategories(data.result || []);
    setLoading(false);
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  const handleAdd = async () => {
    const data = await apiFetch("/categories", {
      method: "POST",
      body: JSON.stringify(form),
    });
    if (data.code === 1000) {
      setMsg("Thêm danh mục thành công!");
      setShowForm(false);
      setForm({ name: "", description: "" });
      load();
    } else {
      setMsg("Lỗi: " + data.message);
    }
  };

  const handleDelete = async (id, name) => {
    if (!confirm(`Xóa danh mục "${name}"?`)) return;
    await apiFetch(`/categories/${id}`, { method: "DELETE" });
    setMsg(`Đã xóa "${name}"`);
    load();
  };

  return (
    <div>
      {msg && <div style={s.toast}>{msg}</div>}
      <button onClick={() => setShowForm(!showForm)} style={s.btnPrimary}>
        {showForm ? "✕ Đóng" : "+ Thêm danh mục"}
      </button>
      {showForm && (
        <div style={s.formBox}>
          <h3 style={{ marginBottom: 16 }}>Thêm danh mục mới</h3>
          <div style={s.formGrid}>
            <FormField
              label="Tên danh mục"
              value={form.name}
              onChange={(v) => setForm((f) => ({ ...f, name: v }))}
            />
            <FormField
              label="Mô tả"
              value={form.description}
              onChange={(v) => setForm((f) => ({ ...f, description: v }))}
            />
          </div>
          <button onClick={handleAdd} style={{ ...s.btnPrimary, marginTop: 8 }}>
            💾 Lưu
          </button>
        </div>
      )}
      {loading ? (
        <p style={s.loading}>Đang tải...</p>
      ) : (
        <table style={s.table}>
          <thead>
            <tr>
              {["ID", "Tên danh mục", "Mô tả", ""].map((h) => (
                <th key={h} style={s.th}>
                  {h}
                </th>
              ))}
            </tr>
          </thead>
          <tbody>
            {categories.map((c) => (
              <tr key={c.id} style={s.tr}>
                <td style={s.td}>{c.id}</td>
                <td style={s.td}>
                  <b>{c.name}</b>
                </td>
                <td style={s.td}>{c.description || "—"}</td>
                <td style={s.td}>
                  <button
                    onClick={() => handleDelete(c.id, c.name)}
                    style={s.btnDanger}
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

function FormField({ label, value, onChange, type = "text" }) {
  return (
    <div style={s.fieldWrap}>
      <label style={s.label}>{label}</label>
      <input
        type={type}
        value={value}
        onChange={(e) => onChange(e.target.value)}
        style={s.input}
      />
    </div>
  );
}

const s = {
  layout: {
    display: "flex",
    minHeight: "100vh",
    fontFamily: "'Segoe UI', sans-serif",
    background: "#f4f6fb",
  },
  sidebar: {
    width: 220,
    background: "#1a1f2e",
    color: "#fff",
    display: "flex",
    flexDirection: "column",
    padding: "24px 16px",
    position: "sticky",
    top: 0,
    height: "100vh",
  },
  brand: { fontSize: 20, fontWeight: 800, color: "#fff", marginBottom: 4 },
  brandSub: {
    fontSize: 11,
    color: "#8892a4",
    marginBottom: 32,
    textTransform: "uppercase",
    letterSpacing: 1,
  },
  nav: { display: "flex", flexDirection: "column", gap: 6, flex: 1 },
  navBtn: {
    background: "transparent",
    border: "none",
    color: "#8892a4",
    padding: "10px 14px",
    borderRadius: 10,
    cursor: "pointer",
    textAlign: "left",
    fontSize: 14,
    display: "flex",
    gap: 10,
    alignItems: "center",
  },
  navBtnActive: { background: "#2d3548", color: "#fff" },
  logoutBtn: {
    background: "transparent",
    border: "1px solid #2d3548",
    color: "#8892a4",
    padding: "10px 14px",
    borderRadius: 10,
    cursor: "pointer",
    fontSize: 13,
  },
  main: { flex: 1, padding: "32px", overflowY: "auto" },
  topBar: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 28,
  },
  pageTitle: { fontSize: 22, fontWeight: 700, color: "#1a1f2e", margin: 0 },
  adminBadge: {
    background: "#fff",
    border: "1px solid #e0e0e0",
    borderRadius: 20,
    padding: "6px 14px",
    fontSize: 13,
    color: "#555",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    background: "#fff",
    borderRadius: 14,
    overflow: "hidden",
    boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
  },
  th: {
    background: "#f8f9fc",
    padding: "12px 16px",
    textAlign: "left",
    fontSize: 12,
    fontWeight: 600,
    color: "#666",
    textTransform: "uppercase",
    letterSpacing: 0.5,
  },
  tr: { borderBottom: "1px solid #f0f0f0" },
  td: { padding: "12px 16px", fontSize: 14, color: "#333" },
  badge: {
    padding: "3px 10px",
    borderRadius: 20,
    fontSize: 12,
    fontWeight: 600,
  },
  btnDanger: {
    background: "#fff0f0",
    color: "#e53e3e",
    border: "1px solid #fecaca",
    padding: "5px 12px",
    borderRadius: 8,
    cursor: "pointer",
    fontSize: 13,
  },
  btnPrimary: {
    background: "#1a1f2e",
    color: "#fff",
    border: "none",
    padding: "10px 20px",
    borderRadius: 10,
    cursor: "pointer",
    fontSize: 14,
    marginBottom: 16,
  },
  loading: { color: "#888", padding: 20 },
  toast: {
    background: "#d4edda",
    color: "#155724",
    border: "1px solid #c3e6cb",
    borderRadius: 10,
    padding: "10px 16px",
    marginBottom: 16,
    fontSize: 14,
  },
  formBox: {
    background: "#fff",
    borderRadius: 14,
    padding: 24,
    marginBottom: 24,
    boxShadow: "0 2px 12px rgba(0,0,0,0.06)",
  },
  formGrid: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: 16,
    marginBottom: 12,
  },
  fieldWrap: { display: "flex", flexDirection: "column", marginBottom: 12 },
  label: { fontSize: 13, fontWeight: 600, color: "#444", marginBottom: 6 },
  input: {
    padding: "10px 12px",
    borderRadius: 8,
    border: "1.5px solid #e0e0e0",
    fontSize: 14,
    outline: "none",
    color: "#333",
  },
  denied: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    justifyContent: "center",
    minHeight: "100vh",
    gap: 8,
    color: "#333",
  },
};
