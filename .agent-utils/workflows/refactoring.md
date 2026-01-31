---
description: Workflow for refactoring code in Restaurant Ordering System
---

# Refactoring Workflow

This workflow guides the AI agent through safe refactoring without breaking functionality.

## Steps

1. **Define Scope**
   - Identify what needs refactoring
   - Define clear goals (readability, performance, structure)
   - List files affected

2. **Pre-Refactoring Checks**
   - Ensure all tests pass
   - Document current behavior
   - Identify public APIs that cannot change

3. **Refactoring Rules**
   - Do NOT change public API contracts
   - All existing tests MUST pass
   - Follow patterns in `01_architecture.md`
   - Follow standards in `02_coding_standards.md`

4. **Common Refactoring Patterns**

   ### Extract Method
   - Keep methods < 30 lines
   - Use descriptive names

   ### Extract Service
   - If controller has business logic → move to service
   - If service is too large → split by responsibility

   ### Improve Entity Design
   - Add proper relationships
   - Add computed fields if needed
   - Ensure @Version on Order

   ### Optimize Queries
   - Replace N+1 with @EntityGraph
   - Add pagination for list queries
   - Add partial indexes

5. **Verification**
   - Run all tests
   - Check WebSocket events still work
   - Check role guards still correct

6. **Documentation**
   - Update JavaDoc if method signatures changed
   - Note significant changes in `MEMORY.md`

## Do's and Don'ts

### ✅ DO
- Make small, incremental changes
- Run tests after each change
- Use IDE refactoring tools

### ❌ DON'T
- Change multiple things at once
- Remove tests (update them instead)
- Change API contracts without discussion
