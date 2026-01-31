# BÁO CÁO CHI PHÍ VẬN HÀNH & TRIỂN KHAI

- **Dự án:** Restaurant Ordering Backend
- **Ngày báo cáo:** 31/01/2026
- **Giả định quy mô:**
    - Hệ thống sử dụng nội bộ cho một nhà hàng quy mô nhỏ.
    - Lượng truy cập thấp, ổn định (~20 đơn hàng/ngày).
    - Ưu tiên hàng đầu là chi phí thấp, ổn định và dễ quản lý để bàn giao cho khách hàng.

### Tóm tắt

Mô hình được đề xuất là **"All-in-One VPS"**, nơi tất cả các thành phần (Frontend, Backend, Database) được triển khai trên một máy chủ ảo duy nhất. Đây là phương án có chi phí thấp, dễ dự đoán và phù hợp nhất để bàn giao cho khách hàng thương mại không có đội ngũ kỹ thuật chuyên sâu.

-   **Chi phí triển khai (một lần):** Ước tính **4 - 8 giờ làm việc** của lập trình viên.
-   **Chi phí vận hành (hàng tháng):** Ước tính **$7 - $10 / tháng** (khoảng 175.000 - 250.000 VNĐ).

---

### 1. Chi Phí Triển Khai (Thanh toán 1 lần)

Đây là chi phí nhân công để cài đặt và cấu hình hệ thống ban đầu. Chi phí này không phải là chi phí server.

| Hạng mục công việc | Thời gian ước tính | Ghi chú |
| :--- | :--- | :--- |
| 1. Tạo tài khoản, cài đặt & bảo mật VPS | 1 - 2 giờ | Cài đặt hệ điều hành, cấu hình tường lửa (firewall). |
| 2. Cài đặt môi trường | 1 - 2 giờ | Cài đặt Nginx, PostgreSQL, Java. |
| 3. Triển khai ứng dụng & Database | 1 - 2 giờ | Deploy frontend, backend, import dữ liệu ban đầu. |
| 4. Cấu hình tên miền & SSL | 0.5 - 1 giờ | Trỏ tên miền về IP của VPS, cài đặt chứng chỉ SSL miễn phí (Let's Encrypt). |
| 5. Kiểm thử & Bàn giao | 0.5 - 1 giờ | Kiểm tra toàn bộ hệ thống và bàn giao tài liệu, tài khoản cho khách hàng. |
| **Tổng cộng** | **4 - 8 giờ làm việc** | **Chi phí sẽ phụ thuộc vào đơn giá giờ công của lập trình viên/đơn vị triển khai.** |

---

### 2. Chi Phí Vận Hành (Thanh toán hàng tháng/hàng năm)

Đây là chi phí cố định mà khách hàng sẽ trả cho các nhà cung cấp dịch vụ để duy trì hoạt động của hệ thống.

| Hạng mục | Nhà cung cấp đề xuất | Chi phí ước tính (USD) | Chi phí ước tính (VNĐ) | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| **1. Máy chủ ảo (VPS)** | DigitalOcean, Vultr, Linode | $5 - $7 / tháng | ~ 125.000 - 175.000 | Gói cơ bản (1 CPU, 1GB RAM, 25GB SSD). Hoàn toàn đủ cho quy mô hiện tại. |
| **2. Tên miền (.com)** | Namecheap, GoDaddy | ~$1 / tháng | ~ 25.000 | Thanh toán theo năm (~$12/năm). |
| **3. Sao lưu tự động (Backup)**| Dịch vụ của nhà cung cấp VPS | $1 - $2 / tháng | ~ 25.000 - 50.000 | Rất quan trọng để phòng mất dữ liệu. Thường bằng 20% chi phí VPS. |
| **4. Bảo trì, giám sát** | (Tùy chọn) | Theo thỏa thuận | Theo thỏa thuận | Chi phí nhân công để kiểm tra, cập nhật hệ thống định kỳ (nếu khách hàng yêu cầu). |
| **Tổng chi phí cố định** | | **$7 - $10 / tháng** | **~ 175.000 - 250.000 / tháng** | **Đây là tổng chi phí hàng tháng mà khách hàng phải trả.** |

---

### 3. Diễn giải và Phân tích

*   **Tại sao lại chọn mô hình này?**
    *   **Minh bạch & Dễ đoán:** Khách hàng biết chính xác họ phải trả bao nhiêu mỗi tháng, không có chi phí bất ngờ.
    *   **Đơn giản để quản lý:** Chỉ có một hóa đơn duy nhất từ nhà cung cấp VPS. Việc quản lý một server đơn giản hơn nhiều so với quản lý nhiều dịch vụ cloud phức tạp.
    *   **Dễ bàn giao:** Toàn bộ hệ thống nằm trong một tài khoản duy nhất thuộc sở hữu của khách hàng, giúp việc chuyển giao quyền quản trị trở nên minh bạch và an toàn.

*   **Khả năng mở rộng:**
    *   Khi nhà hàng phát triển và có lượng truy cập cao hơn, việc nâng cấp VPS (ví dụ lên gói 2 CPU, 2GB RAM) có thể được thực hiện chỉ với vài cú nhấp chuột và hệ thống chỉ gián đoạn vài phút. Chi phí nâng cấp cũng rất hợp lý (thường lên gói $10-$15/tháng).

*   **Kết luận cuối cùng:**
    *   Với tổng chi phí vận hành chỉ khoảng **200.000 VNĐ/tháng**, mô hình **VPS All-in-One** là lựa chọn **tối ưu tuyệt đối** về mặt chi phí và sự đơn giản, đáp ứng hoàn hảo yêu cầu của một giải pháp thương mại ổn định để bàn giao cho khách hàng.
