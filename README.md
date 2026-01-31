# Restaurant Ordering System - Backend

> Cost-optimized ordering system for small/family restaurants.

## Overview

| Spec | Value |
|------|-------|
| **Capacity** | 21 tables, 4 areas (A, B, C, VIP) |
| **Volume** | ~20 orders/day |
| **Stack** | Spring Boot 3.5.10, PostgreSQL/H2, WebSocket STOMP |
| **Auth** | JWT with RBAC (ADMIN / CASHIER / STAFF) |

## Quick Start

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 15+ (production) or H2 (development)

### Run Locally (H2)
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Access Points
| URL | Description |
|-----|-------------|
| http://localhost:8080 | API Base |
| http://localhost:8080/swagger-ui.html | Swagger UI |
| http://localhost:8080/h2-console | H2 Console (dev only) |

### Sample Users (Dev)
| Email | Password | Role |
|-------|----------|------|
| admin@restaurant.com | admin123 | ADMIN |
| cashier@restaurant.com | cashier123 | CASHIER |
| staff@restaurant.com | staff123 | STAFF |

---

## Roles & Permissions

| Role | Permissions |
|------|-------------|
| **ADMIN** | CRUD Area/Table/Menu/Users, Disable tables |
| **CASHIER** | Confirm orders, Payment, Transfer, Print |
| **STAFF** | Open orders, Add items |

---

## API Endpoints

### Authentication
```
POST /auth/login      # Login → JWT token
GET  /auth/me         # Current user info
```

### Admin
```
POST   /admin/areas           # Create area
GET    /admin/areas           # List areas
PATCH  /admin/areas/{id}      # Update area
POST   /admin/tables          # Create table
GET    /admin/tables          # List tables
PATCH  /admin/tables/{id}     # Update table
PATCH  /admin/tables/{id}/disable
PATCH  /admin/tables/{id}/enable
```

### Tables & Menu
```
GET /tables           # List active tables (STAFF/CASHIER)
GET /menu/items       # List menu items
GET /menu/items/{id}  # Menu item with options
```

### Ordering
```
POST   /orders/open                    # Open/get order for table
GET    /orders/open?tableId=           # Get open order
POST   /orders/{id}/items              # Add item
PATCH  /orders/{id}/items/{itemId}     # Update item (CASHIER)
DELETE /orders/{id}/items/{itemId}     # Remove item (CASHIER)
POST   /orders/{id}/confirm            # Confirm (CASHIER)
POST   /orders/{id}/status             # Update status (CASHIER)
GET    /pos/orders?status=             # POS dashboard (CASHIER)
POST   /orders/{id}/transfer           # Transfer items (CASHIER)
```

### Payments
```
POST /payments/cash              # Cash payment
POST /payments/initiate          # E-wallet initiate
POST /payments/momo/callback     # MoMo webhook
POST /payments/zalopay/callback  # ZaloPay webhook
```

### Print
```
GET /orders/{id}/print/provisional  # Provisional bill
GET /orders/{id}/print/receipt      # Final receipt
```

---

## WebSocket Events

### Topics
| Topic | Subscribers |
|-------|-------------|
| `/topic/pos/orders` | CASHIER (POS dashboard) |
| `/topic/tables/{tableId}` | STAFF at table |
| `/topic/orders/{orderId}` | Order-specific updates |

### Event Types
- `ORDER_CREATED`
- `ORDER_ITEM_ADDED`
- `ORDER_CONFIRMED`
- `ORDER_STATUS_CHANGED`
- `PAYMENT_UPDATED`
- `TABLE_TRANSFERRED`

---

## Project Structure

```
src/main/java/com/restaurant/backend/
├── config/           # Security, WebSocket, CORS
├── common/           # DTOs, Enums, Exceptions
├── auth/             # Login, JWT
├── admin/            # Area, Table, User management
├── catalog/          # Menu items, Options
├── ordering/         # Orders, Items, Transfers
├── payment/          # Cash, E-wallet callbacks
├── realtime/         # WebSocket events
└── audit/            # Audit logging
```

---

## Build & Deploy

### Build
```bash
./mvnw clean package -DskipTests
```

### Run Production
```bash
java -jar target/restaurant-backend-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

### Environment Variables (Production)
| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | PostgreSQL connection URL |
| `DATABASE_USER` | Database username |
| `DATABASE_PASSWORD` | Database password |
| `JWT_SECRET` | Secret for JWT signing |

---

## Documentation

Detailed documentation is in `.agent-utils/`:

| File | Purpose |
|------|---------|
| `Description.md` | Full system specification |
| `00_project_context.md` | Roles, features, phases |
| `01_architecture.md` | Tech stack, folder structure |
| `06_development_workflow.md` | 10-phase implementation guide |

---

## Business Rules

1. **1 table = 1 open order** at a time
2. Staff can only **append** items (no delete/payment/transfer)
3. Cashier must **confirm** before COOKING status
4. Order closes when **paidTotal >= grandTotal**
5. Cannot **disable** table with open order

---

## Testing

```bash
# Run all tests
./mvnw test

# Run specific test
./mvnw test -Dtest=OrderServiceTest
```

---

## License

Proprietary - Internal Use Only