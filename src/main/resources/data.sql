-- Sample data for SmartHub Logistics System

-- Insert sample deliveries
INSERT INTO deliveries (tracking_code, customer_name, hub_code, status, cod_amount, created_at) VALUES
('RK-2026-001', 'Nguyen Van A', 'HN-01', 'IN_TRANSIT', 500000.00, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('RK-2026-002', 'Tran Thi B', 'SG-02', 'DELIVERED', 1200000.00, CURRENT_TIMESTAMP - INTERVAL '5 days'),
('RK-2026-003', 'Le Van C', 'DN-03', 'DELAYED', 0.00, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('RK-2026-004', 'Pham Thi D', 'HN-01', 'DAMAGED', 350000.00, CURRENT_TIMESTAMP - INTERVAL '3 days'),
('RK-2026-005', 'Hoang Van E', 'SG-02', 'IN_TRANSIT', 800000.00, CURRENT_TIMESTAMP - INTERVAL '1 day'),
('RK-2026-006', 'Vu Thi F', 'HN-02', 'DELIVERED', 0.00, CURRENT_TIMESTAMP - INTERVAL '7 days'),
('RK-2026-007', 'Dinh Van G', 'DN-03', 'IN_TRANSIT', 2500000.00, CURRENT_TIMESTAMP - INTERVAL '4 hours'),
('RK-2026-008', 'Bui Thi H', 'SG-02', 'DELAYED', 150000.00, CURRENT_TIMESTAMP - INTERVAL '2 days'),
('RK-2026-009', 'Ngo Van I', 'HN-01', 'DELIVERED', 600000.00, CURRENT_TIMESTAMP - INTERVAL '6 days'),
('RK-2026-010', 'Duong Thi K', 'DN-03', 'IN_TRANSIT', 0.00, CURRENT_TIMESTAMP - INTERVAL '12 hours');

-- Insert sample incidents
INSERT INTO incidents (tracking_code, incident_type, hub_code, severity, description, status, created_at) VALUES
('RK-2026-003', 'GIAO_TRỄ', 'DN-03', 'MEDIUM', 'Đơn hàng giao chậm do điều phối sai tuyến', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '1 day'),
('RK-2026-004', 'HỎNG_HÓC', 'HN-01', 'CRITICAL', 'Hàng hóa bị ướt do bảo quản kém tại kho', 'IN_PROGRESS', CURRENT_TIMESTAMP - INTERVAL '3 days'),
('RK-2026-008', 'GIAO_TRỄ', 'SG-02', 'LOW', 'Giao chậm 1 ngày do tắc đường', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '2 days'),
('RK-2026-001', 'THẤT_LẠC', 'HN-01', 'HIGH', 'Đơn hàng bị thất lạc trong quá trình vận chuyển', 'OPEN', CURRENT_TIMESTAMP - INTERVAL '2 days');
