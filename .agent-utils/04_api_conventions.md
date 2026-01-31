# API Conventions

> **AI Agent Instruction**: Follow these API design conventions for Restaurant Ordering System.

---

## 1. RESTful URL Patterns

### URL Structure
```
/auth/{action}                    # Authentication
/admin/{resource}                 # Admin-only operations
/admin/{resource}/{id}            # Admin with ID
/{resource}                       # Public/authenticated resources
/{resource}/{id}                  # Resource by ID
/orders/{orderId}/{action}        # Order actions
/pos/{resource}                   # POS-specific endpoints
/payments/{action}                # Payment operations
/payments/{provider}/callback     # Payment callbacks
```

### Endpoint Patterns

| Action | HTTP Method | URL Pattern | Example |
|--------|-------------|-------------|---------|
| Login | `POST` | `/auth/login` | `POST /auth/login` |
| Get current user | `GET` | `/auth/me` | `GET /auth/me` |
| Create (Admin) | `POST` | `/admin/{resource}` | `POST /admin/areas` |
| List (Admin) | `GET` | `/admin/{resource}?filters` | `GET /admin/tables?areaId=1` |
| Update | `PATCH` | `/admin/{resource}/{id}` | `PATCH /admin/areas/1` |
| Toggle status | `PATCH` | `/admin/{resource}/{id}/{action}` | `PATCH /admin/tables/1/disable` |
| List | `GET` | `/{resource}` | `GET /tables` |
| Get by ID | `GET` | `/{resource}/{id}` | `GET /menu/items/5` |
| Open order | `POST` | `/orders/open` | `POST /orders/open` |
| Add item | `POST` | `/orders/{id}/items` | `POST /orders/1/items` |
| Action | `POST` | `/orders/{id}/{action}` | `POST /orders/1/confirm` |
| Delete item | `DELETE` | `/orders/{id}/items/{itemId}` | `DELETE /orders/1/items/5` |

---

## 2. Complete API Reference

### Authentication
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/auth/login` | PUBLIC | Login and get JWT |
| GET | `/auth/me` | AUTHENTICATED | Get current user info |

### Admin: Area Management
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/admin/areas` | ADMIN | Create area |
| GET | `/admin/areas` | ADMIN | List all areas |
| PATCH | `/admin/areas/{id}` | ADMIN | Update area |

### Admin: Table Management
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/admin/tables` | ADMIN | Create table |
| GET | `/admin/tables?areaId=` | ADMIN | List tables (filter by area) |
| PATCH | `/admin/tables/{id}` | ADMIN | Update table |
| PATCH | `/admin/tables/{id}/disable` | ADMIN | Disable table |
| PATCH | `/admin/tables/{id}/enable` | ADMIN | Enable table |

### Public Tables
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/tables` | STAFF, CASHIER | List active tables (STAFF cannot see DISABLED) |

### Menu Catalog
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/menu/items` | STAFF, CASHIER | List all menu items |
| GET | `/menu/items/{id}` | STAFF, CASHIER | Get menu item with options |

### Ordering
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/orders/open` | STAFF, CASHIER | Open or get existing order for table |
| GET | `/orders/open?tableId=` | STAFF, CASHIER | Get open order for table |
| POST | `/orders/{orderId}/items` | STAFF, CASHIER | Add item to order |
| PATCH | `/orders/{orderId}/items/{itemId}` | CASHIER | Update order item |
| DELETE | `/orders/{orderId}/items/{itemId}` | CASHIER | Remove item from order |
| POST | `/orders/{orderId}/confirm` | CASHIER | Confirm order (→ COOKING) |
| POST | `/orders/{orderId}/status` | CASHIER | Update order status |

### POS Dashboard
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/pos/orders?status=` | CASHIER | Get orders by status for POS |

### Transfers
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/orders/{orderId}/transfer` | CASHIER | Transfer items between tables |

### Payments
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| POST | `/payments/cash` | CASHIER | Pay with cash |
| POST | `/payments/initiate` | CASHIER | Initiate e-wallet payment |
| POST | `/payments/momo/callback` | PUBLIC | MoMo callback webhook |
| POST | `/payments/zalopay/callback` | PUBLIC | ZaloPay callback webhook |

### Print
| Method | Endpoint | Role | Description |
|--------|----------|------|-------------|
| GET | `/orders/{orderId}/print/provisional` | CASHIER | Get provisional bill |
| GET | `/orders/{orderId}/print/receipt` | CASHIER | Get final receipt |

---

## 3. Query Parameters

### Pagination Parameters
| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `page` | int | `0` | Page number (0-indexed) |
| `size` | int | `20` | Items per page |
| `sort` | string | `createdAt,desc` | Sort field and direction |

### Order Filters
| Parameter | Type | Values |
|-----------|------|--------|
| `status` | string | `PENDING_CONFIRM`, `COOKING`, `DONE`, `SERVED`, `CANCELLED` |
| `tableId` | long | Table ID |
| `isClosed` | boolean | `true`, `false` |

### Table Filters
| Parameter | Type | Description |
|-----------|------|-------------|
| `areaId` | long | Filter by area |
| `status` | string | `AVAILABLE`, `OCCUPIED`, `DISABLED` |

---

## 4. Request/Response Formats

### Login Request
```json
POST /auth/login
{
    "username": "staff@restaurant.com",
    "password": "staff123"
}
```

### Login Response
```json
{
    "success": true,
    "data": {
        "token": "eyJhbGc...",
        "type": "Bearer",
        "role": "STAFF",
        "userId": 3,
        "username": "staff@restaurant.com"
    }
}
```

### Open Order Request
```json
POST /orders/open
{
    "tableId": 5
}
```

### Open Order Response
```json
{
    "success": true,
    "data": {
        "id": 123,
        "tableId": 5,
        "tableCode": "B3",
        "areaCode": "B",
        "status": "PENDING_CONFIRM",
        "items": [],
        "subTotal": 0,
        "grandTotal": 0,
        "paidTotal": 0,
        "isClosed": false,
        "createdAt": "2026-01-28T10:00:00Z",
        "createdBy": 3
    }
}
```

### Add Item Request
```json
POST /orders/123/items
{
    "menuItemId": 10,
    "quantity": 2,
    "note": "Ít đá",
    "options": [
        { "optionId": 5, "quantity": 1 }
    ]
}
```

### Add Item Response
```json
{
    "success": true,
    "data": {
        "id": 456,
        "orderId": 123,
        "menuItemId": 10,
        "name": "Cà phê sữa đá",
        "unitPrice": 25000,
        "quantity": 2,
        "note": "Ít đá",
        "options": [
            { "id": 789, "name": "Size L", "priceDelta": 5000 }
        ],
        "status": "PENDING",
        "lineTotal": 60000
    }
}
```

### Cash Payment Request
```json
POST /payments/cash
{
    "orderId": 123,
    "amount": 150000
}
```

### Transfer Request
```json
POST /orders/123/transfer
{
    "type": "MOVE_ITEMS",
    "toTableId": 8,
    "items": [
        { "orderItemId": 456, "quantity": 1 }
    ],
    "note": "Khách đổi bàn"
}
```

---

## 5. Standard Response Format

### Success Response (Single Object)
```json
{
    "success": true,
    "message": "Order created successfully",
    "data": { ... },
    "timestamp": "2026-01-28T10:00:00Z"
}
```

### Success Response (Paginated)
```json
{
    "success": true,
    "data": {
        "content": [ ... ],
        "pageNumber": 0,
        "pageSize": 20,
        "totalElements": 100,
        "totalPages": 5,
        "last": false
    }
}
```

### Error Response
```json
{
    "success": false,
    "message": "Order with id 123 not found",
    "timestamp": "2026-01-28T10:00:00Z",
    "path": "/orders/123"
}
```

### Conflict Response (Optimistic Locking)
```json
{
    "success": false,
    "message": "Order was modified by another user. Please refresh and try again.",
    "timestamp": "2026-01-28T10:00:00Z",
    "path": "/orders/123/items"
}
```

---

## 6. HTTP Status Codes

### Success Codes
| Code | When to Use |
|------|-------------|
| `200 OK` | GET, PATCH success |
| `201 Created` | POST success (new resource) |
| `204 No Content` | DELETE success |

### Error Codes
| Code | When to Use |
|------|-------------|
| `400 Bad Request` | Validation error, missing fields |
| `401 Unauthorized` | Missing/invalid JWT |
| `403 Forbidden` | No permission for role |
| `404 Not Found` | Resource not found |
| `409 Conflict` | Version conflict, duplicate |
| `422 Unprocessable` | Business rule violation |

---

## 7. WebSocket Specification

### Connection
```javascript
// Connect with JWT
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);
stompClient.connect({ Authorization: 'Bearer ' + token }, onConnected);
```

### Topics
| Topic | Description | Subscribers |
|-------|-------------|-------------|
| `/topic/pos/orders` | All order events | CASHIER |
| `/topic/tables/{tableId}` | Table-specific events | STAFF at table |
| `/topic/orders/{orderId}` | Order-specific events | STAFF, CASHIER |

### Event Types
```javascript
// Event payload structure
{
    "type": "ORDER_ITEM_ADDED",
    "orderId": 123,
    "tableId": 5,
    "data": { ... },
    "timestamp": "2026-01-28T10:00:00Z",
    "actorId": 3
}
```

| Event Type | Trigger | Data |
|------------|---------|------|
| `ORDER_CREATED` | New order opened | Order summary |
| `ORDER_ITEM_ADDED` | Item added to order | OrderItem details |
| `ORDER_ITEM_UPDATED` | Item modified | OrderItem details |
| `ORDER_CONFIRMED` | Cashier confirms | Order status |
| `ORDER_STATUS_CHANGED` | Status update | New status |
| `PAYMENT_UPDATED` | Payment received | Payment details, paidTotal |
| `TABLE_TRANSFERRED` | Items moved | Transfer details |

---

## 8. Authentication Header

```
Authorization: Bearer {jwt_token}
```

### JWT Payload
```json
{
    "sub": "staff@restaurant.com",
    "userId": 3,
    "role": "STAFF",
    "iat": 1706432400,
    "exp": 1706518800
}
```

---

## 9. OpenAPI Documentation

### URLs
```
Development: http://localhost:8080/swagger-ui.html
API Docs:    http://localhost:8080/v3/api-docs
```

### Controller Annotations
```java
@Tag(name = "Order Management", description = "APIs for order operations")
@RestController
@RequestMapping("/orders")
public class OrderController { ... }
```
