# AI Agent Utils - Component Guide

Tài liệu này giải thích chi tiết mục đích và cách sử dụng của từng file trong `.agent-utils` cho **Restaurant Ordering System**.

---

## 1. Core Templates (Nền tảng)

Nhóm file này định hình context của dự án. **Bắt buộc đọc trước khi code**.

### `00_project_context.md`
- **Mục đích**: Tổng quan Business Domain và Scope
- **Nội dung**: Roles (ADMIN/CASHIER/STAFF), Features, 10 Phases, Acceptance Criteria
- **Khi nào đọc**: Trước khi bắt đầu bất kỳ task nào

### `01_architecture.md`
- **Mục đích**: Bản đồ kỹ thuật hệ thống
- **Nội dung**: Tech Stack, Folder Structure, API Endpoints, WebSocket Topics
- **Khi nào đọc**: Khi tạo file mới, cần biết đặt ở đâu

### `02_coding_standards.md`
- **Mục đích**: Quy tắc viết code
- **Nội dung**: Naming conventions, Controller/Service templates, Error handling
- **Khi nào đọc**: Khi viết code mới

### `03_database_conventions.md`
- **Mục đích**: Chuẩn thiết kế database
- **Nội dung**: Entity templates, Flyway migrations, Enums, Indexing
- **Khi nào đọc**: Khi tạo entity hoặc migration mới

### `04_api_conventions.md`
- **Mục đích**: Chuẩn API
- **Nội dung**: URL patterns, Request/Response formats, WebSocket specs
- **Khi nào đọc**: Khi tạo endpoint mới

### `05_testing_standards.md`
- **Mục đích**: Chiến lược testing
- **Nội dung**: Test templates (Unit, Controller, Integration, WebSocket)
- **Khi nào đọc**: Khi viết tests

---

## 2. Process Templates (Quy trình)

### `06_development_workflow.md` ⭐ QUAN TRỌNG NHẤT
- **Mục đích**: Hướng dẫn 10-phase implementation
- **Nội dung**: Checklist chi tiết cho từng phase
- **Khi nào đọc**: Để biết phase nào làm gì

### `07_review_checklist.md`
- **Mục đích**: Quality Gate
- **Nội dung**: Security, Order state machine, Concurrency, WebSocket checks
- **Khi nào đọc**: Trước khi commit

### `08_deployment_guide.md`
- **Mục đích**: Hướng dẫn deploy
- **Nội dung**: Build commands, Environment config, Systemd service
- **Khi nào đọc**: Khi cần deploy

---

## 3. AI Support Files

### `Description.md`
- **Mục đích**: Full system specification
- **Nội dung**: Business flows, 14 entities, API spec, Acceptance criteria
- **Khi nào đọc**: Để hiểu toàn bộ hệ thống

### `MEMORY.md`
- **Mục đích**: Long-term memory của project
- **Nội dung**: Decisions log, Business rules, Phase progress
- **Khi nào đọc/update**: Khi có quyết định quan trọng hoặc hoàn thành phase

### `PROMPTS.md`
- **Mục đích**: Optimized prompts cho từng task
- **Nội dung**: Phase prompts, Feature prompts, Bug fix prompts
- **Khi nào dùng**: Copy prompt khi giao việc cho AI

---

## 4. Workflow Templates (`workflows/`)

- **`new-feature.md`**: Quy trình thêm feature mới
- **`bug-fix.md`**: Quy trình sửa bug
- **`refactoring.md`**: Quy trình refactor
- **`code-review.md`**: Quy trình code review

---

## Quick Reference

### Sample Users (Dev)
| Email | Password | Role |
|-------|----------|------|
| admin@restaurant.com | admin123 | ADMIN |
| cashier@restaurant.com | cashier123 | CASHIER |
| staff@restaurant.com | staff123 | STAFF |

### Key Business Rules
1. **1 bàn = 1 order open** tại một thời điểm
2. Staff chỉ **append** món, không delete/payment/transfer
3. Cashier phải **confirm** trước khi COOKING
4. paidTotal >= grandTotal → **close order**

### Build Commands
```bash
# Development (H2)
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Tests
./mvnw test

# Production build
./mvnw clean package -DskipTests
```
