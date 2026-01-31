# Project Context

> **Hướng dẫn sử dụng**: File này chứa thông tin context của dự án Restaurant Ordering System.

---

## 1. Project Overview

| Field | Value |
|-------|-------|
| **Project Name** | `Restaurant Ordering Backend` |
| **Version** | `0.0.1-SNAPSHOT` |
| **Type** | `Backend API + WebSocket` |
| **Status** | `Development` |
| **Framework** | `Spring Boot 3.5.10` |
| **Java Version** | `21` |

### Description
```
Hệ thống đặt món nhà hàng tối ưu chi phí cho quán nhỏ/gia đình.
- 21 bàn chia 4 khu (A, B, C, VIP)
- ~20 đơn/ngày
- Staff order qua web trên điện thoại
- Thu ngân xác nhận và quản lý realtime
- Hỗ trợ gộp/tách/chuyển món giữa bàn
- Thanh toán tiền mặt + ví điện tử (MoMo/ZaloPay)
```

---

## 2. Business Domain

### Domain Overview
```
Restaurant / F&B Operations - Quản lý đặt món và thanh toán nhà hàng
```

### Key Terminology

| Term | Definition |
|------|------------|
| `Area` | Khu vực trong nhà hàng (A, B, C, VIP) |
| `DiningTable` | Bàn ăn thuộc một Area |
| `Order` | Đơn hàng của một bàn (1 bàn = 1 order open tại 1 thời điểm) |
| `OrderItem` | Món trong đơn hàng |
| `MenuItem` | Món trong thực đơn |
| `OptionGroup` | Nhóm tùy chọn (Size, Topping, etc.) |
| `Option` | Tùy chọn cụ thể (Size L, Extra cheese, etc.) |
| `Payment` | Thanh toán (Cash / E-wallet) |
| `TableTransfer` | Log chuyển món/gộp/tách bàn |
| `Confirm` | Thu ngân xác nhận đơn → báo bếp |
| `POS` | Point of Sale - Màn hình thu ngân |

---

## 3. User Roles & Permissions

| Role | Description | Key Permissions |
|------|-------------|-----------------|
| `ADMIN` | Quản trị viên | CRUD Area/Table/Menu/Users, Disable/Enable tables |
| `CASHIER` | Thu ngân | View orders realtime, Confirm, Payment, Transfer, Print |
| `STAFF` | Nhân viên phục vụ | View active tables, Open/Add items to orders |

### Detailed Permissions

#### ADMIN
- CRUD Area/Khu
- CRUD Table/Bàn (thêm, đổi khu, đổi số lượng)
- Disable/Enable bàn (không disable nếu có order open)
- CRUD Menu items & Options
- CRUD Users

#### CASHIER
- Xem order realtime trên POS
- Confirm order (PENDING_CONFIRM → COOKING)
- Update status (COOKING → DONE → SERVED / CANCELLED)
- Merge/Split tables, Move items
- Print provisional bill & final receipt
- Payment (cash + e-wallet)
- Close order

#### STAFF
- Xem bàn hoạt động (không thấy DISABLED)
- Open order nếu chưa có order open
- Add items vào order (kể cả sau confirm)
- **Không được**: payment, transfer, delete items

---

## 4. Core Features

### Feature 1: `Authentication & RBAC`
- **Description**: JWT login với 3 roles
- **Status**: `Phase 2`
- **Priority**: `High`
- **Endpoints**: `/auth/login`, `/auth/me`

### Feature 2: `Area & Table Management`
- **Description**: Admin quản lý khu vực và bàn
- **Status**: `Phase 3`
- **Priority**: `High`
- **Rules**: Không disable bàn có order open

### Feature 3: `Menu Catalog`
- **Description**: Quản lý thực đơn với options
- **Status**: `Phase 4`
- **Priority**: `High`

### Feature 4: `Ordering`
- **Description**: Tạo order, thêm món, confirm, update status
- **Status**: `Phase 5`
- **Priority**: `Critical`
- **Rules**: 1 bàn = 1 order open, Staff chỉ append món

### Feature 5: `Table Transfers`
- **Description**: Move items, Merge, Split bàn
- **Status**: `Phase 6`
- **Priority**: `Medium`
- **Rules**: Full audit log, không xóa cứng

### Feature 6: `Payments`
- **Description**: Cash + E-wallet (MoMo/ZaloPay)
- **Status**: `Phase 7`
- **Priority**: `High`
- **Rules**: Close order khi paid_total >= grand_total

### Feature 7: `Print`
- **Description**: In tạm tính và hóa đơn
- **Status**: `Phase 8`
- **Priority**: `Medium`

### Feature 8: `Realtime (WebSocket)`
- **Description**: STOMP WebSocket cho POS và Staff
- **Status**: `Phase 9`
- **Priority**: `High`
- **Topics**: `/topic/pos/orders`, `/topic/tables/{id}`, `/topic/orders/{id}`

---

## 5. Implementation Phases

| Phase | Name | Description |
|-------|------|-------------|
| 1 | Skeleton | Spring Boot, BaseEntity, enums, Flyway |
| 2 | Auth + RBAC | JWT, User seed, Role guards |
| 3 | Admin Floor | Area CRUD, Table CRUD + disable rules |
| 4 | Catalog | Menu items + options mapping |
| 5 | Ordering Core | Open order, Add items, Confirm, Status, Totals |
| 6 | Transfers | Move/Merge/Split + audit log |
| 7 | Payments | Cash, E-wallet callback, Close order |
| 8 | Print | HTML template, PDF optional |
| 9 | Realtime | WebSocket STOMP broadcast |
| 10 | Hardening | Optimistic locking, tests, performance |

---

## 6. Development Environment

### Sample Users (Dev Profile)

| Email | Password | Role |
|-------|----------|------|
| admin@restaurant.com | admin123 | ADMIN |
| cashier@restaurant.com | cashier123 | CASHIER |
| staff@restaurant.com | staff123 | STAFF |

### Database Configuration

| Profile | Database | URL |
|---------|----------|-----|
| `dev` | H2 (file mode) | `jdbc:h2:file:./data/restaurant_dev` |
| `prod` | PostgreSQL | `jdbc:postgresql://localhost:5432/restaurant` |

---

## 7. External Dependencies

| Service | Purpose | Integration Type |
|---------|---------|------------------|
| `PostgreSQL 15+` | Production database | JDBC |
| `H2 Database` | Development database | JDBC (file mode) |
| `WebSocket STOMP` | Real-time notifications | Built-in |
| `JWT (jjwt 0.11.5)` | Authentication | Library |
| `MoMo/ZaloPay` | E-wallet payments | Webhook callback |

---

## 8. Project Constraints

- **Performance**: Response time < 500ms, WebSocket < 1s
- **Security**: JWT authentication, Role-based access control
- **Concurrency**: Optimistic locking (@Version) cho Order
- **Cost**: Single VPS deployment (Monolith + Static React)

---

## 9. Acceptance Criteria

- [ ] STAFF mở bàn → có order open hoặc tạo mới
- [ ] STAFF thêm món → POS thấy realtime <1s
- [ ] CASHIER confirm → staff thấy status update
- [ ] CASHIER move item → cả 2 bàn update đúng
- [ ] Cash payment → order close khi đủ tiền
- [ ] E-wallet callback → verify → update paidTotal
- [ ] Disabled table không xuất hiện cho STAFF
- [ ] All endpoints secured đúng role
- [ ] Migrations chạy clean từ đầu
