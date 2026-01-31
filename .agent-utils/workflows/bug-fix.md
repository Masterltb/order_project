---
description: Workflow for fixing bugs in Restaurant Ordering System
---

# Bug Fix Workflow

This workflow guides the AI agent through fixing a bug systematically.

## Steps

1. **Understand the Bug**
   - Read the bug description
   - Identify affected component (ordering, payment, etc.)
   - Check if related to concurrency (@Version)

2. **Reproduce the Issue**
   - Write a failing test that demonstrates the bug
   - If concurrency issue, simulate concurrent access
   - Document exact steps to reproduce

3. **Analyze Root Cause**
   - Identify the faulty code
   - Check business rules in `Description.md`
   - Check if order state machine is violated
   - Check if role guards are correct

4. **Implement Fix**
   - Make minimal changes to fix the bug
   - Ensure fix doesn't break other functionality
   - Follow coding standards in `02_coding_standards.md`

5. **Verify Fix**
   - Run the reproduction test - should pass
   - Run all related tests
   - Check for regressions

6. **Document**
   - Add to Known Issues in `MEMORY.md` if significant
   - Update tests to prevent regression

## Common Bug Categories

### Order State Issues
- Check OrderStatus transitions
- Verify cannot modify closed orders
- Verify only CASHIER can confirm

### Concurrency Issues
- Check @Version on Order entity
- Verify 409 Conflict returned on version mismatch
- Verify retry logic suggested

### Role Guard Issues
- Verify @PreAuthorize annotations
- Check role hierarchy (ADMIN > CASHIER > STAFF)

### WebSocket Issues
- Verify events published after state changes
- Verify correct topics used
- Check event data contains required info
