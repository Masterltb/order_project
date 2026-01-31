---
description: Workflow for code review in Restaurant Ordering System
---

# Code Review Workflow

This workflow guides the AI agent through reviewing code as a strict reviewer.

## Steps

1. **Load Context**
   - Read `.agent-utils/07_review_checklist.md`
   - Understand the feature being reviewed
   - Check business rules in `Description.md`

2. **Security Review**
   - [ ] All endpoints have `@PreAuthorize`
   - [ ] Role guards match requirements:
     - ADMIN: CRUD Area/Table/Menu/User
     - CASHIER: Confirm, Payment, Transfer, Print
     - STAFF: Open/Add items only
   - [ ] No SQL injection vulnerabilities
   - [ ] Payment callbacks verify signatures

3. **Business Logic Review**
   - [ ] Order state machine enforced
   - [ ] 1 table = 1 open order rule
   - [ ] Staff cannot delete/pay/transfer
   - [ ] Cannot modify closed orders
   - [ ] Totals calculated correctly

4. **Concurrency Review**
   - [ ] @Version on Order entity
   - [ ] 409 Conflict on version mismatch
   - [ ] Atomic payment + close order

5. **WebSocket Review**
   - [ ] Events published for all changes
   - [ ] Correct topics used
   - [ ] Events contain necessary data

6. **Code Quality Review**
   - [ ] Constructor injection used
   - [ ] Methods < 30 lines
   - [ ] Descriptive variable names
   - [ ] No hardcoded values
   - [ ] Proper error handling

7. **Database Review**
   - [ ] Migrations follow naming convention
   - [ ] No modification to existing migrations
   - [ ] Proper indexes for queries
   - [ ] N+1 queries avoided

8. **Testing Review**
   - [ ] Unit tests for service methods
   - [ ] Controller tests with role guards
   - [ ] Coverage >= 80%

## Output Format

```markdown
## Code Review Summary

### ✅ Approved Items
- [List what looks good]

### ⚠️ Suggestions
- [List non-blocking suggestions]

### ❌ Must Fix
- [List blocking issues]

### Verdict: APPROVED / NEEDS CHANGES
```
