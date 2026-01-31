# Development Workflow

> **⭐ CRITICAL FILE FOR AI AGENT**: 10-Phase implementation guide for Restaurant Ordering System.

---

## Pre-Development Phase

### Step 0: Load Context
```
Action: Read and understand these files BEFORE writing any code

Required Reading:
1. 00_project_context.md    → Roles, features, phases, acceptance criteria
2. 01_architecture.md       → Folder structure, API endpoints, WebSocket
3. 02_coding_standards.md   → Naming conventions, patterns
4. 03_database_conventions.md → Entity templates, migrations

Key Rules to Remember:
- 1 bàn = 1 order open tại 1 thời điểm
- Staff chỉ append món, không payment/transfer
- Cashier confirm rồi mới COOKING
- @Version cho optimistic locking
- WebSocket event cho mọi thay đổi order
```

### Step 1: Identify Current Phase
```
Action: Determine which phase you're implementing

Phases:
[ ] Phase 1: Skeleton      → BaseEntity, enums, Flyway, error handling
[ ] Phase 2: Auth + RBAC   → User, JWT, role guards
[ ] Phase 3: Admin Floor   → Area, DiningTable, disable rules
[ ] Phase 4: Catalog       → MenuItem, OptionGroup, Option
[ ] Phase 5: Ordering Core → Open order, add items, confirm, status
[ ] Phase 6: Transfers     → Move/Merge/Split, audit log
[ ] Phase 7: Payments      → Cash, e-wallet callback, close order
[ ] Phase 8: Print         → Provisional bill, receipt
[ ] Phase 9: Realtime      → WebSocket STOMP broadcast
[ ] Phase 10: Hardening    → Optimistic locking tests, integration tests
```

---

## Phase 1: Skeleton

### Checklist
```
[ ] Create package structure under com.restaurant.backend
    ├── config/
    ├── common/dto/entity/enums/exception/
    ├── auth/
    ├── admin/area/table/user/
    ├── catalog/
    ├── ordering/
    ├── payment/
    ├── realtime/
    └── audit/

[ ] Create BaseEntity with id, createdAt, updatedAt
[ ] Create all enums (OrderStatus, PaymentMethod, etc.)
[ ] Create ApiResponse, PageResponse DTOs
[ ] Create GlobalExceptionHandler
[ ] Setup Flyway with initial migrations
[ ] Create application-dev.properties (H2)
[ ] Create application-prod.properties (PostgreSQL)
```

---

## Phase 2: Auth + RBAC

### Checklist
```
[ ] Create User entity with username, passwordHash, role
[ ] Create UserRepository
[ ] Create AuthController (login, me)
[ ] Create JwtService (generate, validate token)
[ ] Create JwtAuthenticationFilter
[ ] Create SecurityConfig with role-based rules
[ ] Create seed data (admin, cashier, staff users)
[ ] Test: login returns JWT
[ ] Test: /auth/me returns user info
[ ] Test: role guards work correctly
```

### Key Endpoints
```
POST /auth/login    → { username, password } → { token, role }
GET  /auth/me       → { userId, username, role }
```

---

## Phase 3: Admin Floor Management

### Checklist
```
[ ] Create Area entity + migration
[ ] Create AreaController, AreaService
[ ] Create DiningTable entity + migration
[ ] Create TableController, TableService
[ ] Implement disable/enable with rule: cannot disable if order open
[ ] Seed 4 areas (A, B, C, VIP) + 21 tables
[ ] Test: CRUD areas
[ ] Test: CRUD tables
[ ] Test: disable blocked if order open
[ ] Test: STAFF cannot see DISABLED tables
```

### Key Endpoints
```
POST   /admin/areas              → Create area
GET    /admin/areas              → List areas
PATCH  /admin/areas/{id}         → Update area
POST   /admin/tables             → Create table
GET    /admin/tables?areaId=     → List tables
PATCH  /admin/tables/{id}        → Update table
PATCH  /admin/tables/{id}/disable → Disable
PATCH  /admin/tables/{id}/enable  → Enable
GET    /tables                   → Public list (STAFF sees only active)
```

---

## Phase 4: Catalog

### Checklist
```
[ ] Create MenuItem entity + migration
[ ] Create OptionGroup entity + migration
[ ] Create Option entity + migration
[ ] Create MenuItemOptionGroup mapping
[ ] Create MenuController, MenuService
[ ] Seed sample menu items with options
[ ] Test: list menu items
[ ] Test: get menu item with options
```

### Key Endpoints
```
GET /menu/items       → List all menu items
GET /menu/items/{id}  → Get item with options
```

---

## Phase 5: Ordering Core

### Checklist
```
[ ] Create Order entity with @Version
[ ] Create OrderItem entity
[ ] Create OrderItemOption entity
[ ] Create OrderController, PosController
[ ] Create OrderService with:
    - openOrder(tableId) → returns existing or creates new
    - addItem(orderId, request)
    - confirmOrder(orderId)
    - updateStatus(orderId, status)
[ ] Implement totals calculation (subTotal, grandTotal)
[ ] Test: open order for table
[ ] Test: add items updates totals
[ ] Test: confirm changes status to COOKING
[ ] Test: cannot add to closed order
```

### Key Endpoints
```
POST   /orders/open               → Open/get order for table
GET    /orders/open?tableId=      → Get open order
POST   /orders/{id}/items         → Add item
PATCH  /orders/{id}/items/{itemId}→ Update item (CASHIER)
DELETE /orders/{id}/items/{itemId}→ Remove item (CASHIER)
POST   /orders/{id}/confirm       → Confirm (CASHIER)
POST   /orders/{id}/status        → Update status (CASHIER)
GET    /pos/orders?status=        → POS dashboard (CASHIER)
```

---

## Phase 6: Transfers

### Checklist
```
[ ] Create TableTransfer entity
[ ] Create TableTransferItem entity
[ ] Create TransferService
[ ] Implement transfer types:
    - MOVE_ITEMS: move some items to another table
    - MERGE: combine two orders
    - SPLIT: split items to new order
[ ] Rule: If target table has no order, create one
[ ] Rule: Log everything to TableTransfer + TableTransferItem
[ ] Test: move items between tables
[ ] Test: audit log created correctly
```

### Key Endpoint
```
POST /orders/{id}/transfer → { type, toTableId, items[], note }
```

---

## Phase 7: Payments

### Checklist
```
[ ] Create Payment entity
[ ] Create PaymentController
[ ] Create PaymentService
[ ] Implement cash payment:
    - Add payment record
    - Update order.paidTotal
    - If paidTotal >= grandTotal → close order
[ ] Implement e-wallet initiate:
    - Generate payUrl / qrData
    - Return to client
[ ] Implement callback verification:
    - Verify signature (mock for now)
    - Update payment status
    - Update order.paidTotal
[ ] Test: cash payment closes order
[ ] Test: partial payment keeps order open
[ ] Test: callback updates paidTotal
```

### Key Endpoints
```
POST /payments/cash              → { orderId, amount }
POST /payments/initiate          → { orderId, method } → { payUrl }
POST /payments/momo/callback     → Public webhook
POST /payments/zalopay/callback  → Public webhook
```

---

## Phase 8: Print

### Checklist
```
[ ] Create PrintController (or add to OrderController)
[ ] Create HTML template for provisional bill
[ ] Create HTML template for final receipt
[ ] Optionally: PDF export
[ ] Test: provisional bill shows all items
[ ] Test: receipt shows payment info
```

### Key Endpoints
```
GET /orders/{id}/print/provisional → HTML/PDF provisional bill
GET /orders/{id}/print/receipt     → HTML/PDF final receipt
```

---

## Phase 9: Realtime (WebSocket)

### Checklist
```
[ ] Create WebSocketConfig
[ ] Create WebSocketEventPublisher
[ ] Create OrderEvent DTO
[ ] Publish events from OrderService:
    - ORDER_CREATED
    - ORDER_ITEM_ADDED
    - ORDER_ITEM_UPDATED
    - ORDER_CONFIRMED
    - ORDER_STATUS_CHANGED
    - PAYMENT_UPDATED
    - TABLE_TRANSFERRED
[ ] Test: POS receives event within 1 second
[ ] Test: Staff receives table-specific events
```

### Topics
```
/topic/pos/orders        → All order events (CASHIER)
/topic/tables/{tableId}  → Table-specific events (STAFF)
/topic/orders/{orderId}  → Order-specific events (ALL)
```

---

## Phase 10: Hardening

### Checklist
```
[ ] Test optimistic locking:
    - Concurrent add items → one gets 409
    - Update with stale version → ConflictException
[ ] Integration tests for full order flow
[ ] Performance baseline:
    - API response < 500ms
    - WebSocket event < 1s
[ ] Review all role guards
[ ] Check all edge cases:
    - Disabled table not shown to STAFF
    - Cannot disable table with open order
    - Cannot modify closed order
[ ] Clean up console logs
[ ] Verify Flyway migrations run from scratch
```

---

## Quick Reference

### Sample Users (Dev)
| Email | Password | Role |
|-------|----------|------|
| admin@restaurant.com | admin123 | ADMIN |
| cashier@restaurant.com | cashier123 | CASHIER |
| staff@restaurant.com | staff123 | STAFF |

### Build Commands
```bash
# Run with dev profile (H2)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test

# Build JAR
./mvnw clean package -DskipTests

# Run JAR with prod profile
java -jar target/restaurant-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Common Pitfalls
| Pitfall | Prevention |
|---------|------------|
| Forgot @Version on Order | Check 03_database_conventions.md |
| Not publishing WebSocket event | Always call eventPublisher.publish() |
| Wrong role guard | Check 00_project_context.md for permissions |
| Missing migration | Create new Vxx__description.sql |
