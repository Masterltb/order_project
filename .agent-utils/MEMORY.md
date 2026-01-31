# Project Memory

> **AI Agent Instruction**: Read this file to understand the project's evolution, key decisions, and critical context. Update this file when you make significant decisions or discoveries.

---

## 1. Project History & Decisions Log

| Date | Decision | Context | Rationale | Impact |
|------|----------|---------|-----------|--------|
| `2026-01-28` | **Project initialized** | New restaurant ordering system | Cost-optimized for small restaurant | Clean slate |
| `2026-01-28` | **Monolith architecture** | Small scale (~20 orders/day) | Single VPS, cost-optimized | Simple deployment |
| `2026-01-28` | **WebSocket STOMP** | Need realtime updates | No external service needed | Native Spring support |
| `2026-01-28` | **Optimistic locking** | Multiple staff add items | Prevent race conditions | @Version on Order |
| `2026-01-28` | **H2 for dev, PostgreSQL for prod** | Cost optimization | No Docker needed for dev | Faster local development |

---

## 2. Business Rules (Critical)

### Order Rules
1. **1 bàn = 1 order open** tại một thời điểm
2. Staff chỉ **append** món, không tạo nhiều order
3. Staff **không được** payment, transfer, delete items
4. Cannot add items to **closed** orders
5. Cannot modify **CANCELLED** orders

### Table Rules
1. Cannot disable table with **open order**
2. STAFF cannot see **DISABLED** tables
3. Table status auto-updates:
   - AVAILABLE → OCCUPIED (when order created)
   - OCCUPIED → AVAILABLE (when order closed)

### Payment Rules
1. paidTotal >= grandTotal → **close order** automatically
2. Payment callbacks must **verify signature** before processing
3. All payments are **additive** (partial payments allowed)

### Transfer Rules
1. All transfers **logged** to TableTransfer + TableTransferItem
2. If target table has no order → **create order first**
3. Never hard-delete, only move/reassign

---

## 3. Technical Debt & Workarounds

> **Note**: Temporary fixes that need to be addressed later.

- _None yet - project just initialized_

---

## 4. Performance Baselines

| Metric | Target |
|--------|--------|
| API Response Time | < 500ms (P95) |
| WebSocket Event Delivery | < 1 second |
| Database Query | < 50ms (with indexes) |
| Concurrent Staff | ~5 simultaneous |

---

## 5. Known Issues

- _None yet - project just initialized_

---

## 6. Environment Specifics

| Environment | Database | Notes |
|-------------|----------|-------|
| **Local (dev)** | H2 file mode | No Docker needed |
| **Production** | PostgreSQL | VPS same server or external |

---

## 7. Lessons Learned

| Topic | Learning | Action Item |
|-------|----------|-------------|
| _To be filled as project progresses_ | | |

---

## 8. Phase Progress Tracker

| Phase | Name | Status | Notes |
|-------|------|--------|-------|
| 1 | Skeleton | ⬜ Not started | BaseEntity, enums, Flyway |
| 2 | Auth + RBAC | ⬜ Not started | JWT, User, roles |
| 3 | Admin Floor | ⬜ Not started | Area, Table |
| 4 | Catalog | ⬜ Not started | Menu, Options |
| 5 | Ordering Core | ⬜ Not started | Open, Add, Confirm |
| 6 | Transfers | ⬜ Not started | Move, Merge, Split |
| 7 | Payments | ⬜ Not started | Cash, E-wallet |
| 8 | Print | ⬜ Not started | Bills, Receipts |
| 9 | Realtime | ⬜ Not started | WebSocket |
| 10 | Hardening | ⬜ Not started | Tests, Performance |

Legend: ⬜ Not started | 🟡 In progress | ✅ Completed
