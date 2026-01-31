# Coding Standards

> **AI Agent Instruction**: Follow these coding conventions EXACTLY when writing code. Any deviation from these standards is considered a bug.

---

## 1. Naming Conventions

### General Rules

| Element | Convention | Example |
|---------|------------|---------|
| **Class** | PascalCase | `OrderService`, `MenuItemController` |
| **Method** | camelCase | `findOpenOrder()`, `addItemToOrder()` |
| **Variable** | camelCase | `orderId`, `tableCode` |
| **Constant** | UPPER_SNAKE_CASE | `MAX_ITEMS_PER_ORDER`, `DEFAULT_PAGE_SIZE` |
| **Package** | lowercase | `com.restaurant.backend.ordering` |
| **Database Table** | snake_case | `dining_table`, `order_item` |
| **Database Column** | snake_case | `grand_total`, `paid_total` |

### Module-Specific Patterns

```
Controller:     OrderController, AreaController, PaymentController
Service:        OrderService, MenuService, PaymentService
ServiceImpl:    OrderServiceImpl, PaymentServiceImpl
Repository:     OrderRepository, MenuItemRepository
Entity:         Order, OrderItem, DiningTable, MenuItem
DTO Request:    OpenOrderRequest, AddItemRequest, CashPaymentRequest
DTO Response:   OrderResponse, OrderItemResponse, TableResponse
```

---

## 2. File Structure Templates

### Controller (Order Management)
```java
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for order operations")
public class OrderController {
    
    private final OrderService orderService;
    
    // GET endpoints first
    @GetMapping("/open")
    @Operation(summary = "Get open order for table")
    @PreAuthorize("hasAnyRole('STAFF', 'CASHIER')")
    public ApiResponse<OrderResponse> getOpenOrder(@RequestParam Long tableId) { ... }
    
    // POST endpoints second
    @PostMapping("/open")
    @Operation(summary = "Open or get existing order for table")
    @PreAuthorize("hasAnyRole('STAFF', 'CASHIER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<OrderResponse> openOrder(@Valid @RequestBody OpenOrderRequest request) { ... }
    
    @PostMapping("/{orderId}/items")
    @Operation(summary = "Add item to order")
    @PreAuthorize("hasAnyRole('STAFF', 'CASHIER')")
    public ApiResponse<OrderItemResponse> addItem(
            @PathVariable Long orderId,
            @Valid @RequestBody AddItemRequest request) { ... }
    
    @PostMapping("/{orderId}/confirm")
    @Operation(summary = "Confirm order - Cashier only")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<OrderResponse> confirmOrder(@PathVariable Long orderId) { ... }
    
    // PATCH/PUT endpoints third
    @PatchMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Update order item - Cashier only")
    @PreAuthorize("hasRole('CASHIER')")
    public ApiResponse<OrderItemResponse> updateItem(...) { ... }
    
    // DELETE endpoints last
    @DeleteMapping("/{orderId}/items/{itemId}")
    @Operation(summary = "Remove item from order - Cashier only")
    @PreAuthorize("hasRole('CASHIER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long orderId, @PathVariable Long itemId) { ... }
}
```

### Service Interface
```java
public interface OrderService {
    // Query methods first
    Optional<Order> findOpenOrderByTableId(Long tableId);
    OrderResponse getOrderById(Long orderId);
    Page<OrderResponse> findOrdersByStatus(OrderStatus status, Pageable pageable);
    
    // Command methods second
    OrderResponse openOrder(Long tableId, Long staffId);
    OrderItemResponse addItem(Long orderId, AddItemRequest request, Long staffId);
    OrderResponse confirmOrder(Long orderId, Long cashierId);
    OrderResponse updateStatus(Long orderId, OrderStatus status, Long cashierId);
    void closeOrder(Long orderId);
}
```

### Service Implementation
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final DiningTableRepository tableRepository;
    private final MenuItemRepository menuItemRepository;
    private final WebSocketEventPublisher eventPublisher;
    
    @Override
    @Transactional
    public OrderResponse openOrder(Long tableId, Long staffId) {
        log.info("[ORDER_OPEN] tableId={}, staffId={}", tableId, staffId);
        
        // Check for existing open order
        Optional<Order> existing = orderRepository.findByTableIdAndIsClosedFalse(tableId);
        if (existing.isPresent()) {
            log.info("[ORDER_EXISTING] orderId={}", existing.get().getId());
            return toResponse(existing.get());
        }
        
        // Create new order
        DiningTable table = tableRepository.findById(tableId)
            .orElseThrow(() -> new ResourceNotFoundException("DiningTable", tableId));
        
        Order order = new Order();
        order.setTable(table);
        order.setStatus(OrderStatus.PENDING_CONFIRM);
        order.setCreatedBy(staffId);
        
        Order saved = orderRepository.save(order);
        log.info("[ORDER_CREATED] orderId={}, tableId={}", saved.getId(), tableId);
        
        // Publish event
        eventPublisher.publish(OrderEvent.created(saved));
        
        return toResponse(saved);
    }
    
    @Override
    @Transactional
    public OrderItemResponse addItem(Long orderId, AddItemRequest request, Long staffId) {
        log.info("[ORDER_ITEM_ADD] orderId={}, menuItemId={}, qty={}", 
                orderId, request.getMenuItemId(), request.getQuantity());
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
        
        if (order.getIsClosed()) {
            throw new BusinessException("Cannot add items to closed order");
        }
        
        // ... add item logic
        
        eventPublisher.publish(OrderEvent.itemAdded(order, savedItem));
        return toResponse(savedItem);
    }
}
```

---

## 3. Error Handling Pattern

### Exception Hierarchy
```
BusinessException (abstract)
├── ResourceNotFoundException      → 404: Order/Table/MenuItem not found
├── ValidationException           → 400: Invalid quantity, missing fields
├── ConflictException            → 409: Version conflict, table already has order
├── OrderClosedException         → 422: Cannot modify closed order
├── TableDisabledException       → 422: Table is disabled
└── InsufficientPaymentException → 422: Payment amount < remaining balance
```

### Error Response Format
```json
{
    "success": false,
    "message": "Order with id 123 not found",
    "timestamp": "2026-01-28T10:00:00Z",
    "path": "/orders/123"
}
```

### Exception Handling
```java
// ❌ DON'T: Generic exception
throw new RuntimeException("Order not found");

// ✅ DO: Specific exception with context
throw new ResourceNotFoundException("Order", orderId);

// ✅ DO: Business-specific exception
throw new OrderClosedException(orderId);
```

---

## 4. Logging Standards

### Log Levels

| Level | Usage |
|-------|-------|
| `ERROR` | Payment failed, Database error |
| `WARN` | Conflict detected, Invalid request |
| `INFO` | Order created, Item added, Payment received |
| `DEBUG` | Detailed flow for debugging |

### Logging Pattern
```java
// ✅ DO: Structured logging for order events
log.info("[ORDER_CREATED] orderId={}, tableId={}, staffId={}", orderId, tableId, staffId);
log.info("[ORDER_ITEM_ADDED] orderId={}, menuItemId={}, qty={}", orderId, menuItemId, qty);
log.info("[ORDER_CONFIRMED] orderId={}, cashierId={}", orderId, cashierId);
log.info("[PAYMENT_RECEIVED] orderId={}, method={}, amount={}", orderId, method, amount);
log.warn("[ORDER_CONFLICT] orderId={}, expectedVersion={}, actualVersion={}", orderId, expected, actual);
```

---

## 5. Design Patterns Used

```
[x] Repository Pattern      - Data access abstraction
[x] Service Layer          - Business logic encapsulation  
[x] DTO Pattern            - Data transfer between layers
[x] Builder Pattern        - Complex DTO construction
[x] Observer Pattern       - WebSocket event publishing
[x] Optimistic Locking     - Concurrency control with @Version
```

---

## 6. Do's and Don'ts

### ✅ DO

- Use constructor injection (`@RequiredArgsConstructor`)
- Keep methods under 30 lines
- Write descriptive variable names (`orderId`, not `id`)
- Add JavaDoc for public APIs
- Use `Optional` instead of null checks
- Validate input at controller level (`@Valid`)
- Log all state transitions (create, confirm, payment)
- Use `@Version` on Order entity for optimistic locking
- Publish WebSocket events for all order changes

### ❌ DON'T

- Use field injection (`@Autowired` on fields)
- Use magic numbers (use `DEFAULT_PAGE_SIZE = 10`)
- Catch `Exception` - catch specific exceptions
- Mix business logic in controllers
- Return entities directly from controllers
- Use `@Transactional` without specifying `readOnly`
- Allow negative quantity or prices
- Modify closed orders
- Hardcode table configurations

---

## 7. Code Quality Checklist

Before committing, verify:

- [ ] No hardcoded values (use constants or config)
- [ ] All public methods have proper documentation
- [ ] Error messages are user-friendly
- [ ] Logging is appropriate for audit trail
- [ ] No commented-out code
- [ ] Order state transitions are validated
- [ ] WebSocket events are published for changes
- [ ] Role-based access is properly enforced
- [ ] Optimistic locking is used for Order updates
