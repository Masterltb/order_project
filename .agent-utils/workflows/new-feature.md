---
description: Workflow for developing a new feature in Restaurant Ordering System
---

# New Feature Development Workflow

This workflow guides the AI agent through implementing a new feature.

## Steps

1. **Context Loading**
   - Read `.agent-utils/00_project_context.md`
   - Read `.agent-utils/01_architecture.md`
   - Read `.agent-utils/06_development_workflow.md`
   - Read `.agent-utils/MEMORY.md`

2. **Identify Phase**
   - Determine which phase this feature belongs to (1-10)
   - Check phase checklist in `06_development_workflow.md`

3. **Requirement Analysis**
   - Create a plan outlining the changes
   - Identify affected components:
     - Entities needed
     - DTOs needed
     - Service methods
     - Controller endpoints
     - WebSocket events

4. **Database Design (if applicable)**
   - Create migration: `Vxx__description.sql`
   - Create Entity classes with proper annotations
   - Add Enums if needed
   - Verify with `03_database_conventions.md`

5. **Implementation**
   - **Service Layer**:
     - Implement business logic
     - Use `@Transactional` for writes
     - Validate business rules
   - **DTOs**: Request/Response with validation
   - **Controller**: Endpoints with role guards
   - **WebSocket**: Publish events for changes

6. **Testing**
   - Write Unit Tests for Service (>80% coverage)
   - Write Controller Tests with role checks
   - Write Integration Tests for critical paths
   - Test WebSocket event publishing

7. **Review & Verify**
   - Check against `07_review_checklist.md`
   - Verify role guards correct
   - Verify WebSocket events published
   - Run all tests

8. **Documentation**
   - Update Swagger annotations
   - Update `MEMORY.md` with key decisions
   - Mark phase progress in `MEMORY.md`
