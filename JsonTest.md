### AUTH

**Register:**
```json
{
  "username": "testuser",
  "password": "Password123",
  "email": "test@example.com",
  "fullName": "Nguyen Van Test",
  "phone": "0987654321",
  "address": "Ha Noi, Viet Nam"
}
```

**Login:**
```json
{
  "username": "testuser",
  "password": "Password123"
}
```

**Introspect (Check status):**
```json
{
  "token": "YOUR_TOKEN_HERE"
}
```

**Logout:**
```json
{
  "token": "YOUR_TOKEN_HERE"
}
```

**Refresh (Renew token):**
```json
{
  "token": "YOUR_TOKEN_HERE"
}
```

### CATEGORY

{
"name": "Office Mouse",
"description": "Chuột văn phòng nhẹ, bền, pin lâu"
}

{
"name": "Gaming Mouse",
"description": "Chuột chuyên game FPS, DPI cao"
}

{
"name": "Wireless Mouse",
"description": "Chuột không dây"
}


### PRODUCT
[
  {
    "name": "Logitech G304",
    "description": "Wireless gaming mouse",
    "price": 890000.0,
    "stock": 100,
    "categoryId": 1
  },
  {
    "name": "Razer Basilisk V3",
    "description": "Gaming RGB mouse",
    "price": 1990000.0,
    "stock": 40,
    "categoryId": 2
  },
  {
    "name": "Logitech M331 Silent",
    "description": "Chuột văn phòng siêu êm",
    "price": 350000.0,
    "stock": 200,
    "categoryId": 1
  },
  {
    "name": "SteelSeries Rival 3",
    "description": "Chuột gaming giá rẻ hiệu năng cao",
    "price": 750000.0,
    "stock": 50,
    "categoryId": 2
  }
]


### CART (Add Item)
```json
{
  "productId": 1,
  "quantity": 2
}
```

### ORDER (Place Order)
```json
{
  "shippingAddress": "123 Duong ABC, Quan 1, TP.HCM"
}
```
