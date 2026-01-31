# Database Conventions

> **AI Agent Instruction**: Follow these database conventions when creating migrations, entities, and queries.

---

## 1. Naming Conventions

### Tables

| Element | Convention | Example |
|---------|------------|---------|
| **Table Name** | snake_case, singular | `dining_table`, `order_item` |
| **Junction Table** | `{table1}_{table2}` | `menu_item_option_group` |
| **Column Name** | snake_case | `grand_total`, `paid_total` |
| **Primary Key** | `id` | `id BIGSERIAL PRIMARY KEY` |
| **Foreign Key** | `{referenced_table}_id` | `order_id`, `menu_item_id` |
| **Index** | `idx_{table}_{column}` | `idx_order_status` |
| **Unique Constraint** | `uk_{table}_{column}` | `uk_user_username` |

---

## 2. Migration Strategy

### Tool: `Flyway`

### File Naming
```
V{version}__{description}.sql

Examples:
V1__create_area_table.sql
V2__create_dining_table.sql
V3__create_user_table.sql
V4__create_menu_item_table.sql
V5__create_option_group_table.sql
V6__create_option_table.sql
V7__create_menu_item_option_group.sql
V8__create_order_table.sql
V9__create_order_item_table.sql
V10__create_order_item_option.sql
V11__create_payment_table.sql
V12__create_table_transfer.sql
V13__create_table_transfer_item.sql
V14__create_audit_log_table.sql
V15__seed_areas_tables.sql
V16__seed_sample_users.sql
```

### Migration Rules

> [!CAUTION]
> **NEVER** modify existing migration files after they've been applied.

```sql
-- ✅ DO: Create new migration for changes
-- V17__add_image_url_to_menu_item.sql
ALTER TABLE menu_item ADD COLUMN image_url VARCHAR(500);

-- ❌ DON'T: Modify V4__create_menu_item_table.sql
```

---

## 3. Data Types Standards

### Recommended Types

| Purpose | PostgreSQL | Java Type | Example |
|---------|------------|-----------|---------|
| **ID** | `BIGSERIAL` | `Long` | `id BIGSERIAL PRIMARY KEY` |
| **Short Text** | `VARCHAR(n)` | `String` | `name VARCHAR(200)` |
| **Long Text** | `TEXT` | `String` | `note TEXT` |
| **Boolean** | `BOOLEAN` | `Boolean` | `is_closed BOOLEAN DEFAULT false` |
| **Timestamp** | `TIMESTAMPTZ` | `Instant` | `created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()` |
| **Money** | `DECIMAL(12,2)` | `BigDecimal` | `grand_total DECIMAL(12,2)` |
| **Quantity** | `INTEGER` | `Integer` | `quantity INTEGER NOT NULL` |
| **Status** | `VARCHAR(50)` | `Enum` | `status VARCHAR(50) NOT NULL` |
| **JSON** | `JSONB` | `String` | `payload JSONB` |
| **Version** | `INTEGER` | `Integer` | `version INTEGER NOT NULL DEFAULT 0` |

---

## 4. Entity Templates

### Area Entity
```java
@Entity
@Table(name = "area")
@Getter @Setter @NoArgsConstructor
public class Area extends BaseEntity {
    
    @Column(nullable = false, length = 10, unique = true)
    private String code;  // A, B, C, VIP
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private AreaStatus status = AreaStatus.ACTIVE;
    
    @OneToMany(mappedBy = "area")
    private List<DiningTable> tables = new ArrayList<>();
}
```

### DiningTable Entity
```java
@Entity
@Table(name = "dining_table")
@Getter @Setter @NoArgsConstructor
public class DiningTable extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id", nullable = false)
    private Area area;
    
    @Column(name = "table_code", nullable = false, length = 10)
    private String tableCode;  // A1, B8, VIP3
    
    private Integer capacity = 4;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TableStatus status = TableStatus.AVAILABLE;
}
```

### Order Entity (with Optimistic Locking)
```java
@Entity
@Table(name = "orders")  // "order" is reserved keyword
@Getter @Setter @NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Order extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_id", nullable = false)
    private DiningTable table;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private OrderStatus status = OrderStatus.PENDING_CONFIRM;
    
    @Column(name = "sub_total", precision = 12, scale = 2)
    private BigDecimal subTotal = BigDecimal.ZERO;
    
    @Column(name = "grand_total", precision = 12, scale = 2)
    private BigDecimal grandTotal = BigDecimal.ZERO;
    
    @Column(name = "paid_total", precision = 12, scale = 2)
    private BigDecimal paidTotal = BigDecimal.ZERO;
    
    @Column(name = "is_closed", nullable = false)
    private Boolean isClosed = false;
    
    @Column(name = "created_by")
    private Long createdBy;
    
    @Column(name = "confirmed_by")
    private Long confirmedBy;
    
    @Column(name = "confirmed_at")
    private Instant confirmedAt;
    
    // Optimistic Locking
    @Version
    private Integer version;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    @OneToMany(mappedBy = "order")
    private List<Payment> payments = new ArrayList<>();
}
```

### OrderItem Entity
```java
@Entity
@Table(name = "order_item")
@Getter @Setter @NoArgsConstructor
public class OrderItem extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
    
    @Column(name = "name_snapshot", nullable = false, length = 200)
    private String nameSnapshot;  // Snapshot at order time
    
    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false)
    private Integer quantity = 1;
    
    @Column(columnDefinition = "TEXT")
    private String note;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30, nullable = false)
    private OrderItemStatus status = OrderItemStatus.PENDING;
    
    @OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemOption> options = new ArrayList<>();
}
```

### Payment Entity
```java
@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor
public class Payment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentMethod method;  // CASH, MOMO, ZALOPAY
    
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;
    
    @Column(name = "provider_txn_id", length = 100)
    private String providerTxnId;
    
    @Column(columnDefinition = "JSONB")
    private String payload;
    
    @Column(name = "paid_at")
    private Instant paidAt;
}
```

---

## 5. SQL Migration Templates

### Area Table
```sql
-- V1__create_area_table.sql
CREATE TABLE area (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    sort_order INTEGER DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_area_sort ON area(sort_order);
```

### DiningTable Table
```sql
-- V2__create_dining_table.sql
CREATE TABLE dining_table (
    id BIGSERIAL PRIMARY KEY,
    area_id BIGINT NOT NULL REFERENCES area(id),
    table_code VARCHAR(10) NOT NULL,
    capacity INTEGER DEFAULT 4,
    status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    
    UNIQUE(area_id, table_code)
);

CREATE INDEX idx_dining_table_area ON dining_table(area_id);
CREATE INDEX idx_dining_table_status ON dining_table(status);
```

### Order Table
```sql
-- V8__create_order_table.sql
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    table_id BIGINT NOT NULL REFERENCES dining_table(id),
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_CONFIRM',
    sub_total DECIMAL(12,2) DEFAULT 0,
    grand_total DECIMAL(12,2) DEFAULT 0,
    paid_total DECIMAL(12,2) DEFAULT 0,
    is_closed BOOLEAN NOT NULL DEFAULT false,
    created_by BIGINT,
    confirmed_by BIGINT,
    confirmed_at TIMESTAMPTZ,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_order_table ON orders(table_id);
CREATE INDEX idx_order_status ON orders(status) WHERE is_closed = false;
CREATE INDEX idx_order_open ON orders(table_id) WHERE is_closed = false;
```

---

## 6. Enums

### OrderStatus
```java
public enum OrderStatus {
    PENDING_CONFIRM,  // Staff đã tạo, chờ cashier confirm
    COOKING,          // Cashier đã confirm, đang nấu
    DONE,            // Bếp xong
    SERVED,          // Đã phục vụ
    CANCELLED        // Đã hủy
}
```

### OrderItemStatus
```java
public enum OrderItemStatus {
    PENDING,    // Chờ xác nhận
    COOKING,    // Đang nấu
    DONE,       // Xong
    SERVED,     // Đã phục vụ
    CANCELLED   // Hủy
}
```

### PaymentMethod
```java
public enum PaymentMethod {
    CASH,
    MOMO,
    ZALOPAY
}
```

### PaymentStatus
```java
public enum PaymentStatus {
    PENDING,
    SUCCEEDED,
    FAILED
}
```

### TableStatus
```java
public enum TableStatus {
    AVAILABLE,   // Trống
    OCCUPIED,    // Có khách (có order open)
    DISABLED     // Tạm khóa (admin)
}
```

---

## 7. Indexing Strategy

### When to Create Indexes

| Scenario | Create Index? |
|----------|---------------|
| Primary Key | ✅ Automatic |
| Foreign Key (table_id, order_id) | ✅ Yes |
| Status column (frequent filtering) | ✅ Yes |
| Composite (status + is_closed) | ✅ Yes |
| created_at for ordering | ⚠️ Consider |

### Partial Indexes (Recommended)
```sql
-- Only index open orders
CREATE INDEX idx_order_open ON orders(table_id) WHERE is_closed = false;

-- Only index pending items
CREATE INDEX idx_order_item_pending ON order_item(order_id) WHERE status = 'PENDING';
```

---

## 8. Query Optimization Rules

### ✅ DO

```java
// Use specific queries
@Query("SELECT o FROM Order o WHERE o.table.id = :tableId AND o.isClosed = false")
Optional<Order> findOpenByTableId(@Param("tableId") Long tableId);

// Use @EntityGraph for eager loading
@EntityGraph(attributePaths = {"items", "items.options"})
Optional<Order> findWithItemsById(Long id);

// Paginate large results
Page<Order> findByStatus(OrderStatus status, Pageable pageable);
```

### ❌ DON'T

```java
// N+1 Query
orders.forEach(o -> o.getItems().size());  // loads items one by one

// Load all columns when only need few
repository.findAll();  // when only need id and status

// Load without pagination
repository.findByStatus(status);  // could be huge
```
