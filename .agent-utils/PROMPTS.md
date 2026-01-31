# Optimized AI Prompts

> **AI Agent Instruction**: Use these prompts to get the best results for Restaurant Ordering System development.

---

## 1. Phase-Based Prompts

### Phase 1: Skeleton
```markdown
I need to implement the project skeleton for Restaurant Ordering System.

**Requirements**:
- Create package structure under com.restaurant.backend
- Create BaseEntity with id, createdAt, updatedAt
- Create all enums (OrderStatus, PaymentMethod, etc.)
- Create ApiResponse and PageResponse DTOs
- Create GlobalExceptionHandler
- Setup Flyway with application-dev.properties (H2)

**Context**:
- Follow `01_architecture.md` for folder structure
- Follow `03_database_conventions.md` for entity patterns

**Output**:
- Package structure created
- Base classes implemented
- Flyway configured
```

### Phase 2: Auth + RBAC
```markdown
I need to implement authentication and role-based access control.

**Requirements**:
- Create User entity with username, passwordHash, role
- Create AuthController (login, me)
- Create JwtService (generate, validate)
- Create SecurityConfig with role-based rules
- Seed sample users (admin, cashier, staff)

**Context**:
- Roles: ADMIN, CASHIER, STAFF
- Follow `00_project_context.md` for role permissions

**Output**:
- Login returns JWT token
- /auth/me returns user info
- Role guards work correctly
```

### Phase 5: Ordering Core
```markdown
I need to implement the core ordering functionality.

**Requirements**:
- Create Order, OrderItem, OrderItemOption entities
- Implement openOrder(tableId) - returns existing or creates new
- Implement addItem(orderId, request)
- Implement confirmOrder(orderId) - CASHIER only
- Calculate totals (subTotal, grandTotal)
- Publish WebSocket events

**Business Rules**:
- 1 table = 1 open order at a time
- Staff can only append items
- Only CASHIER can confirm
- Cannot add to closed orders

**Context**:
- Use @Version for optimistic locking
- Follow `06_development_workflow.md` Phase 5 checklist
```

---

## 2. Feature Development Prompt

```markdown
I need to implement [Feature Name] for Restaurant Ordering System.

**Requirements**:
[Paste specific requirements]

**Context**:
- Read `00_project_context.md` for business context
- Read `01_architecture.md` for folder structure
- Read `02_coding_standards.md` for coding patterns

**Task**:
1. Analyze requirements against existing code
2. Create/modify database schema (if needed)
3. Implement Service layer with business logic
4. Create DTOs and Controller endpoints
5. Publish WebSocket events
6. Write unit tests

**Output Constraints**:
- Use Java 21 features
- Follow error handling patterns in `02_coding_standards.md`
- Ensure 80% test coverage
```

---

## 3. Bug Fix Prompt

```markdown
I found a bug in [Component Name].

**Issue Description**:
[Describe the bug, provide logs or error messages]

**Steps to Reproduce**:
1. [Step 1]
2. [Step 2]

**Task**:
1. Analyze root cause
2. Create reproduction test case
3. Fix the bug
4. Verify with test case
5. Check for regressions

**Output**:
- Root cause explanation
- Test case code
- Fixed code
```

---

## 4. Code Review Prompt

```markdown
Please review the following code for Restaurant Ordering System.

**Files to Review**:
- [List files]

**Criteria**:
- Check against `07_review_checklist.md`
- Verify role-based access (ADMIN/CASHIER/STAFF)
- Check order state machine transitions
- Verify WebSocket events are published
- Check optimistic locking usage

**Output**:
- Issues found (categorized by severity)
- Suggestions for improvement
- Pass/Fail recommendation
```

---

## 5. WebSocket Event Prompt

```markdown
I need to implement WebSocket event for [Event Type].

**Event**: [ORDER_CREATED, ORDER_ITEM_ADDED, etc.]

**Requirements**:
- Publish to correct topics
- Include necessary data for UI update
- Deliver within 1 second

**Topics**:
- /topic/pos/orders (CASHIER)
- /topic/tables/{tableId} (STAFF at table)
- /topic/orders/{orderId} (specific order)

**Context**:
- Follow `04_api_conventions.md` WebSocket section
```

---

## 6. Payment Integration Prompt

```markdown
I need to implement [Payment Type] for Restaurant Ordering System.

**Type**: [CASH / MOMO / ZALOPAY]

**Requirements**:
- Create Payment entity
- Implement payment endpoint
- For e-wallet: implement callback verification
- Update order.paidTotal
- Close order when paidTotal >= grandTotal
- Publish PAYMENT_UPDATED event

**Context**:
- Check `Description.md` for payment flow
- Verify signatures for e-wallet callbacks
```

---

## 7. Transfer Prompt

```markdown
I need to implement table transfer functionality.

**Transfer Types**:
- MOVE_ITEMS: Move some items to another table
- MERGE: Combine two orders
- SPLIT: Split items to new order

**Requirements**:
- Create TableTransfer and TableTransferItem entities
- Log all transfers (never hard-delete)
- If target table has no order, create one first
- Publish TABLE_TRANSFERRED event

**Context**:
- Only CASHIER can transfer
- Follow `Description.md` Section 3.4
```

---

## 8. Testing Prompt

```markdown
I need to write tests for [Component Name].

**Test Types Needed**:
- [ ] Unit tests for service methods
- [ ] Controller tests with role guards
- [ ] Integration tests for database
- [ ] WebSocket event tests

**Scenarios**:
[List specific scenarios to test]

**Context**:
- Follow `05_testing_standards.md` for templates
- Coverage target: 80%
```
