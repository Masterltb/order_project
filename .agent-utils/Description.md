# Restaurant Ordering System - Full Description

> **AI Agent Instruction**: Complete system specification for Restaurant Ordering Backend.

---

## 1. Domain Context

### Business Environment
- **Type**: Quán nhỏ / gia đình
- **Capacity**: 21 bàn, 4 khu vực
- **Volume**: ~20 đơn/ngày
- **Service Model**: Nhân viên order (khách không tự order)

### Areas Layout
| Area | Tables | Code Range |
|------|--------|------------|
| Khu C | 5 | C1–C5 |
| Khu B | 8 | B1–B8 |
| Khu A | 4 | A1–A4 |
| VIP | 4 | VIP1–VIP4 |

### Operational Goals
1. Tạo order nhanh
2. Thu ngân thấy đơn realtime
3. Gọi thêm món liên tục đến khi thanh toán
4. Hỗ trợ gộp/tách/chuyển món giữa bàn
5. Thanh toán tiền mặt + ví điện tử
6. In tạm tính và hóa đơn

---

## 2. Roles & Permissions (RBAC)

### ADMIN
- CRUD Area/Khu
- CRUD Table/Bàn (thêm, đổi khu, đổi số lượng)
- Disable/Enable bàn (không disable nếu có order open)
- CRUD Menu items & Options
- CRUD Users

### CASHIER
- Xem order realtime trên POS
- Confirm order (PENDING_CONFIRM → COOKING)
- Update status (COOKING → DONE → SERVED / CANCELLED)
- Merge/Split tables, Move items
- Print provisional bill & final receipt
- Payment (cash + e-wallet)
- Close order

### STAFF
- Xem bàn hoạt động (không thấy DISABLED)
- Open order nếu chưa có order open
- Add items vào order (kể cả sau confirm)
- **Không được**: payment, transfer, delete items

---

## 3. Business Flows

### 3.1 Table Management (ADMIN)
1. Admin tạo Area với code/name/sortOrder
2. Admin tạo DiningTable thuộc Area
3. Admin có thể disable bàn (tạm khóa)
4. **Rule**: Không disable nếu bàn đang có order open

### 3.2 Order Creation (STAFF)
1. Staff chọn khu → bàn
2. Backend:
   - Nếu bàn có order open → trả order hiện tại
   - Nếu chưa có → tạo Order mới (status: PENDING_CONFIRM)
3. Staff thêm món (lặp nhiều lần):
   - Chọn menuItem + options + quantity + note
   - Tạo OrderItem + OrderItemOption
4. POS nhận event realtime ORDER_ITEM_ADDED

**Rule**: 1 bàn = 1 order open. Staff chỉ append món.

### 3.3 Order Confirmation (CASHIER)
1. POS hiển thị list order PENDING_CONFIRM
2. Thu ngân confirm → status = COOKING
3. Thu ngân "nói bếp" (ngoài hệ thống)
4. Thu ngân update status:
   - COOKING → DONE → SERVED
   - Hoặc CANCELLED (có lý do)
5. POS và Staff nhận event realtime

### 3.4 Table Transfers (CASHIER)
**Scenarios:**
- **Move items**: Chuyển vài món từ bàn A sang bàn B
- **Merge**: Gộp 2 bàn thành 1 order (thanh toán chung)
- **Split**: Tách 1 phần món sang bàn khác (tách hóa đơn)

**Rules:**
- Log vào TABLE_TRANSFER + TABLE_TRANSFER_ITEM
- Không mất lịch sử, không xóa cứng
- Nếu bàn đích chưa có order → tạo order trước

### 3.5 Payment (CASHIER)
**Cash:**
1. Tạo Payment (method: CASH, amount)
2. Update order.paidTotal
3. Nếu paidTotal >= grandTotal → close order

**E-wallet:**
1. POST /payments/initiate → trả payUrl/qrData
2. Provider gọi webhook callback
3. Backend verify signature
4. Payment status = SUCCEEDED
5. Update order.paidTotal
6. Nếu đủ → close order

**Print:**
- `/print/provisional`: Tạm tính (chưa close)
- `/print/receipt`: Hóa đơn (sau close)

---

## 4. Data Model (14 Entities)

### Area
```
- id (PK)
- code (unique): A, B, C, VIP
- name: Khu A, Khu B...
- sortOrder
- status: ACTIVE / INACTIVE
- createdAt, updatedAt
```

### DiningTable
```
- id (PK)
- areaId (FK → Area)
- tableCode (unique within area): A1, B8...
- capacity
- status: AVAILABLE / OCCUPIED / DISABLED
- createdAt, updatedAt
```

### User
```
- id (PK)
- username (unique)
- passwordHash
- role: ADMIN / CASHIER / STAFF
- status: ACTIVE / BLOCKED
- createdAt, updatedAt
```

### MenuItem
```
- id (PK)
- name
- basePrice
- category
- status: AVAILABLE / UNAVAILABLE
- availableFrom, availableTo (optional hours)
- createdAt, updatedAt
```

### OptionGroup
```
- id (PK)
- name: Size, Topping, Sugar level...
- selectionType: SINGLE / MULTIPLE
- minSelect, maxSelect
- status: ACTIVE / INACTIVE
- createdAt, updatedAt
```

### Option
```
- id (PK)
- optionGroupId (FK)
- name: Size L, Extra cheese...
- priceDelta
- status: ACTIVE / INACTIVE
- createdAt, updatedAt
```

### MenuItemOptionGroup
```
- menuItemId (FK)
- optionGroupId (FK)
- createdAt
```

### Order
```
- id (PK)
- tableId (FK → DiningTable)
- status: PENDING_CONFIRM / COOKING / DONE / SERVED / CANCELLED
- subTotal, grandTotal, paidTotal
- isClosed
- createdBy, confirmedBy
- confirmedAt
- version (optimistic locking)
- createdAt, updatedAt
```

### OrderItem
```
- id (PK)
- orderId (FK)
- menuItemId (FK)
- nameSnapshot (snapshot at order time)
- unitPrice
- quantity
- note
- status: PENDING / COOKING / DONE / SERVED / CANCELLED
- createdAt, updatedAt
```

### OrderItemOption
```
- id (PK)
- orderItemId (FK)
- optionId (FK)
- nameSnapshot
- priceDelta
- quantity
- createdAt
```

### Payment
```
- id (PK)
- orderId (FK)
- method: CASH / MOMO / ZALOPAY
- amount
- status: PENDING / SUCCEEDED / FAILED
- providerTxnId
- payload (JSON)
- paidAt
- createdAt, updatedAt
```

### TableTransfer
```
- id (PK)
- type: MOVE_ITEMS / MERGE / SPLIT
- fromTableId, toTableId
- orderId
- actorId (cashier)
- note
- createdAt
```

### TableTransferItem
```
- id (PK)
- transferId (FK)
- orderItemId (FK)
- movedQty
- createdAt
```

### AuditLog
```
- id (PK)
- actorId
- orderId
- action
- beforeJson, afterJson
- createdAt
```

---

## 5. API Specification

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Login → JWT |
| GET | `/auth/me` | Get current user |

### Admin
| Method | Endpoint | Role |
|--------|----------|------|
| POST/GET/PATCH | `/admin/areas` | ADMIN |
| POST/GET/PATCH | `/admin/tables` | ADMIN |
| PATCH | `/admin/tables/{id}/disable` | ADMIN |
| PATCH | `/admin/tables/{id}/enable` | ADMIN |

### Public
| Method | Endpoint | Role |
|--------|----------|------|
| GET | `/tables` | STAFF, CASHIER |
| GET | `/menu/items` | STAFF, CASHIER |
| GET | `/menu/items/{id}` | STAFF, CASHIER |

### Ordering
| Method | Endpoint | Role |
|--------|----------|------|
| POST | `/orders/open` | STAFF, CASHIER |
| GET | `/orders/open?tableId=` | STAFF, CASHIER |
| POST | `/orders/{id}/items` | STAFF, CASHIER |
| PATCH | `/orders/{id}/items/{itemId}` | CASHIER |
| DELETE | `/orders/{id}/items/{itemId}` | CASHIER |
| POST | `/orders/{id}/confirm` | CASHIER |
| POST | `/orders/{id}/status` | CASHIER |
| GET | `/pos/orders?status=` | CASHIER |
| POST | `/orders/{id}/transfer` | CASHIER |

### Payments
| Method | Endpoint | Role |
|--------|----------|------|
| POST | `/payments/cash` | CASHIER |
| POST | `/payments/initiate` | CASHIER |
| POST | `/payments/momo/callback` | PUBLIC |
| POST | `/payments/zalopay/callback` | PUBLIC |

### Print
| Method | Endpoint | Role |
|--------|----------|------|
| GET | `/orders/{id}/print/provisional` | CASHIER |
| GET | `/orders/{id}/print/receipt` | CASHIER |

---

## 6. WebSocket Events

| Topic | Event Types |
|-------|-------------|
| `/topic/pos/orders` | ORDER_CREATED, ORDER_ITEM_ADDED, ORDER_CONFIRMED, ORDER_STATUS_CHANGED, PAYMENT_UPDATED |
| `/topic/tables/{id}` | ORDER_CREATED, ORDER_ITEM_ADDED, TABLE_TRANSFERRED |
| `/topic/orders/{id}` | All events for specific order |

---

## 7. Acceptance Criteria

- [ ] STAFF mở bàn → có order open hoặc tạo mới
- [ ] STAFF thêm món → POS thấy realtime <1s
- [ ] CASHIER confirm → staff thấy status update
- [ ] CASHIER move item → cả 2 bàn update đúng
- [ ] Cash payment → order close khi đủ tiền
- [ ] E-wallet callback → verify → update paidTotal
- [ ] Disabled table không xuất hiện cho STAFF
- [ ] All endpoints secured đúng role
- [ ] Migrations chạy clean từ đầu