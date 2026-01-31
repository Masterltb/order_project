# Review Checklist

> **AI Agent Instruction**: Use this checklist to self-review code before committing.

---

## 1. Security Checklist

### Authentication & Authorization
- [ ] All endpoints have proper `@PreAuthorize` annotations
- [ ] Role checks match requirements:
  - ADMIN: Area, Table, Menu, User management
  - CASHIER: Confirm, Payment, Transfer, Print
  - STAFF: Open order, Add items only
- [ ] JWT validation in all protected endpoints
- [ ] No sensitive data in logs (passwords, tokens)
- [ ] Payment callbacks verify signatures

### Data Access
- [ ] Users can only access their allowed resources
- [ ] STAFF cannot see DISABLED tables
- [ ] No SQL injection vulnerabilities
- [ ] Input validation on all request DTOs

---

## 2. Order State Machine Checklist

### Valid Transitions
```
PENDING_CONFIRM → COOKING (via confirm)
COOKING → DONE | CANCELLED
DONE → SERVED | CANCELLED
SERVED → (terminal)
CANCELLED → (terminal)
```

### State Rules
- [ ] Only CASHIER can confirm orders
- [ ] Only CASHIER can update status
- [ ] Cannot add items to closed orders
- [ ] Cannot modify CANCELLED orders
- [ ] Status changes are logged/audited

---

## 3. Concurrency Checklist

### Optimistic Locking
- [ ] `@Version` field on Order entity
- [ ] 409 Conflict returned on version mismatch
- [ ] Client-friendly error message with retry suggestion
- [ ] Tests for concurrent modifications

### Race Conditions
- [ ] Multiple staff adding items concurrently handled
- [ ] Payment and close order atomic
- [ ] Table transfer atomic (source and target)

---

## 4. Business Rules Checklist

### Core Rules
- [ ] 1 bàn = 1 order open tại 1 thời điểm
- [ ] Cannot disable table with open order
- [ ] Staff can only append items, not delete
- [ ] Totals (subTotal, grandTotal, paidTotal) calculated correctly
- [ ] paidTotal >= grandTotal → close order
- [ ] All transfers logged (TableTransfer + TableTransferItem)

### Price Calculations
- [ ] Item price = basePrice + sum(option.priceDelta)
- [ ] Line total = item price × quantity
- [ ] SubTotal = sum(line totals)
- [ ] GrandTotal = SubTotal (+ tax/discount if applicable)

---

## 5. WebSocket Checklist

### Event Publishing
- [ ] ORDER_CREATED event on new order
- [ ] ORDER_ITEM_ADDED event on add item
- [ ] ORDER_ITEM_UPDATED event on update item
- [ ] ORDER_CONFIRMED event on confirm
- [ ] ORDER_STATUS_CHANGED event on status change
- [ ] PAYMENT_UPDATED event on payment
- [ ] TABLE_TRANSFERRED event on transfer

### Event Delivery
- [ ] Events published to correct topics
- [ ] Events contain necessary data for UI update
- [ ] Events delivered within 1 second

---

## 6. API Response Checklist

### Format
- [ ] All responses use `ApiResponse` wrapper
- [ ] Paginated responses use `PageResponse`
- [ ] Error responses have clear messages
- [ ] HTTP status codes are correct

### Validation
- [ ] Request DTOs have `@Valid` annotations
- [ ] Field validations (`@NotNull`, `@Min`, etc.)
- [ ] Custom validation messages are user-friendly

---

## 7. Database Checklist

### Migrations
- [ ] Migration file follows naming convention (Vxx__desc.sql)
- [ ] No modification to existing migrations
- [ ] Foreign keys have indexes
- [ ] Soft delete uses `deleted_at` column if applicable

### Queries
- [ ] No N+1 queries (use `@EntityGraph` or JOIN FETCH)
- [ ] Pagination for list queries
- [ ] Projections for list APIs (not full entities)

---

## 8. Logging Checklist

- [ ] INFO log for state changes (order created, confirmed, paid)
- [ ] WARN log for conflicts and validation failures
- [ ] ERROR log for exceptions
- [ ] Structured log format: `[EVENT_TYPE] key=value`
- [ ] No sensitive data in logs

---

## 9. Testing Checklist

- [ ] Unit tests for service methods
- [ ] Controller tests with role-based access
- [ ] Integration tests for critical paths
- [ ] WebSocket event tests
- [ ] Concurrency tests for optimistic locking
- [ ] Coverage >= 80% for new code

---

## 10. Code Quality Checklist

- [ ] No hardcoded values (use constants/config)
- [ ] No commented-out code
- [ ] Methods < 30 lines
- [ ] Descriptive variable names
- [ ] JavaDoc for public APIs
- [ ] Constructor injection (`@RequiredArgsConstructor`)
- [ ] `@Transactional(readOnly = true)` for read methods

---

## Quick Pass/Fail Criteria

| Category | Pass Criteria |
|----------|---------------|
| Security | All role guards correct |
| Business Logic | Order state machine enforced |
| Concurrency | Optimistic locking works |
| Realtime | WebSocket events published |
| Testing | Coverage >= 80% |
