# SmartHub - AI-Powered Smart Logistics Operations Center

Trung tâm vận hành logistics thông minh tích hợp trí tuệ nhân tạo (AI) phục vụ doanh nghiệp RikkeiExpress.

## 🏗️ Kiến trúc hệ thống

Hệ thống được xây dựng theo kiến trúc 4 phân hệ nghiên cứu trọng tâm:

### Module 1: RAG System (Tra cứu tri thức quy chế)
- Nạp tài liệu quy chế vào vector database
- Tìm kiếm ngữ nghĩa và trả lời câu hỏi với trích dẫn nguồn
- Chống ảo tưởng (Anti-Hallucination)

### Module 2: Agent Operations (Xử lý sự cố tự động)
- Phân tích tin nhắn khách hàng bằng ngôn ngữ tự nhiên
- Bóc tách thực thể: mã vận đơn, loại sự cố, bưu cục, mức độ nghiêm trọng
- Tự động tạo phiếu sự cố và cập nhật trạng thái đơn hàng

### Module 3: MCP Integration (Đối soát dữ liệu)
- Tích hợp Model Context Protocol qua Stdio Transport
- Công cụ truy vấn dữ liệu an toàn với SQL Validator
- Xuất báo cáo Markdown tự động

### Module 4: LLMOps Observability (Giám sát vận hành)
- Tích hợp Langfuse để theo dõi vết chạy (Tracing)
- Đo lường thời gian phản hồi (Latency)
- Thống kê tiêu thụ token và chi phí ước tính

## 🛠️ Công nghệ sử dụng

- **Java 17** - Ngôn ngữ nền tảng
- **Spring Boot 3.3.x** - Framework backend
- **Spring AI** - Tích hợp AI và LLM
- **PostgreSQL + Pgvector** - Cơ sở dữ liệu quan hệ và vector
- **OpenAI gpt-4o-mini** - Mô hình ngôn ngữ
- **Langfuse** - Giám sát LLMOps
- **Model Context Protocol (MCP)** - Giao thức mở tích hợp công cụ

## 📋 Cài đặt và chạy

### 1. Cấu hình môi trường

Sao chép file `.env.example` thành `.env` và điền thông tin cấu hình:

```bash
cp .env.example .env
```

Cập nhật các biến môi trường:
- `DB_USERNAME`, `DB_PASSWORD`: Thông tin đăng nhập PostgreSQL
- `OPENAI_API_KEY`: API Key của OpenAI
- `LANGFUSE_PUBLIC_KEY`, `LANGFUSE_SECRET_KEY`: Khóa Langfuse (tùy chọn)

### 2. Cấu hình PostgreSQL

Đảm bảo PostgreSQL đã được cài đặt và extension pgvector đã được kích hoạt:

```sql
CREATE DATABASE smarthub;
\c smarthub
CREATE EXTENSION IF NOT EXISTS vector;
```

### 3. Chạy ứng dụng

Sử dụng Gradle wrapper:

```bash
./gradlew bootRun
```

Hoặc sử dụng IDE để chạy class `NiApplication.java`

Ứng dụng sẽ chạy tại: `http://localhost:8080`

## 🚀 API Endpoints

### Health Check
- `GET /api/v1/health` - Kiểm tra trạng thái hệ thống

### RAG Module (Module 1)
- `POST /api/v1/rag/ingest` - Nạp tài liệu vào vector store
- `GET /api/v1/rag/ask?question=...` - Đặt câu hỏi tra cứu quy chế
- `DELETE /api/v1/rag/clear` - Xóa toàn bộ vector store

### Agent Operations (Module 2)
- `POST /api/v1/operations/chat` - Gửi tin nhắn xử lý sự cố

### Delivery Management
- `POST /api/v1/deliveries` - Tạo đơn hàng mới
- `GET /api/v1/deliveries/{trackingCode}` - Tra cứu đơn hàng
- `GET /api/v1/deliveries` - Lấy danh sách tất cả đơn hàng
- `GET /api/v1/deliveries/hub/{hubCode}` - Lấy đơn hàng theo bưu cục

### Incident Management
- `POST /api/v1/incidents` - Tạo phiếu sự cố
- `GET /api/v1/incidents` - Lấy danh sách sự cố
- `GET /api/v1/incidents/tracking/{trackingCode}` - Lấy sự cố theo mã vận đơn
- `GET /api/v1/incidents/hub/{hubCode}` - Lấy sự cố theo bưu cục
- `GET /api/v1/incidents/status/{status}` - Lấy sự cố theo trạng thái
- `PUT /api/v1/incidents/{id}/status` - Cập nhật trạng thái sự cố

## 📝 Ví dụ sử dụng

### 1. Nạp tài liệu quy chế

```bash
curl -X POST http://localhost:8080/api/v1/rag/ingest \
  -F "file=@sample-quy-che-van-chuyen.txt" \
  -F "documentName=Quy Che Vận Chuyển"
```

### 2. Tra cứu quy chế

```bash
curl "http://localhost:8080/api/v1/rag/ask?question=Phí vận chuyển nội thành là bao nhiêu?"
```

### 3. Xử lý sự cố bằng Agent

```bash
curl -X POST http://localhost:8080/api/v1/operations/chat \
  -H "Content-Type: application/json" \
  -d '{"message": "Đơn RK-2026-001 của tôi bị ướt sũng hỏng đồ ở kho Hà Nội, đề nghị kiểm tra"}'
```

### 4. Tạo đơn hàng mới

```bash
curl -X POST http://localhost:8080/api/v1/deliveries \
  -H "Content-Type: application/json" \
  -d '{
    "trackingCode": "RK-2026-011",
    "customerName": "Nguyen Van X",
    "hubCode": "HN-01",
    "status": "IN_TRANSIT",
    "codAmount": 500000.00
  }'
```

## 🔬 Nghiên cứu và thử nghiệm

### RQ-01: Semantic Search Accuracy
- Thử nghiệm với các kích thước chunk khác nhau: 300, 500, 1000 ký tự
- Điều chỉnh độ chồng lập (overlap): 0%, 10%, 20%
- Tối ưu ngưỡng tương đồng (similarity threshold): 0.5, 0.7, 0.9

### RQ-02: Agentic Autonomous Workflow
- Kiểm tra khả năng bóc tách thực thể từ câu văn tự nhiên
- Đánh giá độ chính xác của Function Calling
- Thử nghiệm với các trường hợp edge case (mã đơn không tồn tại, thiếu thông tin)

### RQ-03: Standardized Protocol Integration
- Kiểm tra kết nối MCP Server qua Stdio Transport
- Xác nhận không có lỗi Stdio Pollution
- Thử nghiệm Safe SQL Validator với các câu lệnh nguy hiểm

### RQ-04: Cost & Latency Optimization
- Theo dõi token usage trên Langfuse Dashboard
- So sánh chi phí giữa RAG và In-Context Learning
- Kiểm tra cơ chế giới hạn vòng lặp vô hạn (max-iterations: 6)

## 📊 Cấu trúc cơ sở dữ liệu

### Bảng `deliveries`
- `id`: Khóa chính
- `tracking_code`: Mã vận đơn (unique)
- `customer_name`: Tên khách hàng
- `hub_code`: Mã bưu cục
- `status`: Trạng thái (IN_TRANSIT, DELIVERED, DELAYED, DAMAGED)
- `cod_amount`: Số tiền thu hộ
- `created_at`: Thời gian tạo

### Bảng `incidents`
- `id`: Khóa chính
- `tracking_code`: Mã vận đơn
- `incident_type`: Loại sự cố (HỎNG_HÓC, GIAO_TRỄ, THẤT_LẠC)
- `hub_code`: Mã bưu cục
- `severity`: Mức độ nghiêm trọng (LOW, MEDIUM, CRITICAL)
- `description`: Mô tả chi tiết
- `status`: Trạng thái xử lý (OPEN, IN_PROGRESS, RESOLVED)
- `created_at`: Thời gian tạo

### Bảng `vector_store`
- `id`: Khóa chính
- `content`: Nội dung văn bản
- `metadata`: Metadata (JSON)
- `embedding`: Vector nhúng (1536 chiều)

## 🔒 Bảo mật

- Không hardcode API keys trong mã nguồn
- Sử dụng environment variables cho thông tin nhạy cảm
- MCP Server chỉ cho phép lệnh SELECT
- Tự động thêm LIMIT 100 cho các câu truy vấn
- Chặn các từ khóa nguy hiểm: DROP, DELETE, UPDATE, ALTER

## 📈 Tiêu chí nghiệm thu

- **Module 1 (25%)**: Ingestion thành công, trả lời đúng chính sách, có trích dẫn nguồn
- **Module 2 (25%)**: Nhận diện đúng sự cố, trích xuất chính xác tham số, tự động cập nhật CSDL
- **Module 3 (25%)**: Kết nối MCP ổn định, kiểm duyệt SQL an toàn, sinh báo cáo Markdown
- **Module 4 (25%)**: Ghi nhận Trace trên Langfuse, hiển thị Latency và Token Usage

## 📞 Hỗ trợ

Để biết thêm chi tiết, vui lòng tham khảo tài liệu SRS-AI-SMARTHUB-V1.0

---

**Phiên bản**: 1.0.0 (Release Candidate)  
**Môn học**: AI Integrated in Action (AI_PTIT_K24)  
**Nền tảng**: Java • Spring Boot • Spring AI • Supabase (Pgvector) • MCP • Langfuse
