-- =============================================================================
-- SEED DATA FOR TSATMS
-- =============================================================================
USE tsatms_db;

-- =============================================================================
-- 1. SLA CONFIGURATION DATA
-- =============================================================================
INSERT INTO sla_configuration (priority, response_sla_minutes, resolution_sla_hours) VALUES
('CRITICAL', 15, 2),
('HIGH', 30, 4),
('MEDIUM', 120, 12),
('LOW', 480, 48);

-- =============================================================================
-- 2. CUSTOMER DATA
-- =============================================================================
INSERT INTO customer (customer_number, customer_name, email, mobile_number, customer_type, city, status) VALUES
('CUST100245', 'Rajesh Kumar', 'rajesh.kumar@email.com', '9876543210', 'ENTERPRISE', 'Mumbai', 'ACTIVE'),
('CUST100246', 'Priya Sharma', 'priya.sharma@email.com', '9876543211', 'CONSUMER', 'Bangalore', 'ACTIVE'),
('CUST100247', 'Amit Patel', 'amit.patel@email.com', '9876543212', 'SME', 'Delhi', 'ACTIVE'),
('CUST100248', 'Neha Gupta', 'neha.gupta@email.com', '9876543213', 'ENTERPRISE', 'Hyderabad', 'ACTIVE'),
('CUST100249', 'Sanjay Verma', 'sanjay.verma@email.com', '9876543214', 'CONSUMER', 'Pune', 'ACTIVE'),
('CUST100250', 'Deepak Singh', 'deepak.singh@email.com', '9876543215', 'SME', 'Chennai', 'ACTIVE'),
('CUST100251', 'Anjali Reddy', 'anjali.reddy@email.com', '9876543216', 'ENTERPRISE', 'Kolkata', 'ACTIVE'),
('CUST100252', 'Vikram Desai', 'vikram.desai@email.com', '9876543217', 'CONSUMER', 'Ahmedabad', 'ACTIVE'),
('CUST100253', 'Shreya Mishra', 'shreya.mishra@email.com', '9876543218', 'CONSUMER', 'Jaipur', 'ACTIVE'),
('CUST100254', 'Arjun Nair', 'arjun.nair@email.com', '9876543219', 'SME', 'Surat', 'ACTIVE');

-- =============================================================================
-- 3. TELECOM SERVICE DATA
-- =============================================================================
INSERT INTO telecom_service (service_code, service_name, service_type, customer_id, activation_date, service_status) VALUES
('SVC001', 'Enterprise Connectivity Mumbai', 'ENTERPRISE_CONNECTIVITY', 1, '2026-01-15', 'ACTIVE'),
('SVC002', 'Mobile Postpaid - Business', 'MOBILE', 1, '2026-02-01', 'ACTIVE'),
('SVC003', 'Broadband Home Bangalore', 'BROADBAND', 2, '2026-01-10', 'ACTIVE'),
('SVC004', 'Mobile Postpaid Individual', 'MOBILE', 2, '2026-03-01', 'ACTIVE'),
('SVC005', 'VPN Corporate Delhi', 'VPN', 3, '2026-01-20', 'ACTIVE'),
('SVC006', 'Cloud Connectivity Enterprise', 'CLOUD_CONNECTIVITY', 4, '2025-12-15', 'ACTIVE'),
('SVC007', 'Mobile Prepaid Chennai', 'MOBILE', 6, '2026-02-14', 'ACTIVE'),
('SVC008', 'Broadband Commercial Kolkata', 'BROADBAND', 7, '2026-01-05', 'ACTIVE'),
('SVC009', 'Enterprise VPN Link', 'VPN', 4, '2026-03-10', 'ACTIVE'),
('SVC010', 'Cloud Storage Subscription', 'CLOUD_CONNECTIVITY', 3, '2026-02-20', 'ACTIVE'),
('SVC011', 'Mobile Corporate Lines', 'MOBILE', 1, '2026-01-01', 'ACTIVE'),
('SVC012', 'Broadband Premium Hyderabad', 'BROADBAND', 4, '2026-02-01', 'ACTIVE'),
('SVC013', 'Enterprise Connectivity Pune', 'ENTERPRISE_CONNECTIVITY', 5, '2026-01-25', 'ACTIVE'),
('SVC014', 'Mobile Postpaid Youth', 'MOBILE', 9, '2026-03-15', 'ACTIVE'),
('SVC015', 'Broadband Business Jaipur', 'BROADBAND', 10, '2026-02-10', 'ACTIVE');

-- =============================================================================
-- 4. NETWORK ENGINEER DATA
-- =============================================================================
INSERT INTO network_engineer (employee_code, engineer_name, specialization, region, experience_years, availability, active_ticket_count) VALUES
('ENG1008', 'Prakash Rao', 'Core Network', 'West', 9, 'AVAILABLE', 2),
('ENG1015', 'Suresh Kumar', 'RAN', 'North', 7, 'BUSY', 4),
('ENG1021', 'Rajesh Menon', 'Broadband', 'South', 6, 'AVAILABLE', 1),
('ENG1030', 'Rohit Sharma', 'IP Network', 'West', 10, 'AVAILABLE', 3),
('ENG1031', 'Anand Verma', 'Core Network', 'North', 8, 'AVAILABLE', 2),
('ENG1032', 'Vikram Singh', 'Broadband', 'West', 7, 'AVAILABLE', 0),
('ENG1033', 'Divya Nair', 'RAN', 'South', 5, 'BUSY', 5),
('ENG1034', 'Sanjay Patil', 'IP Network', 'North', 6, 'ON_LEAVE', 0),
('ENG1035', 'Nitin Desai', 'Core Network', 'South', 9, 'AVAILABLE', 1),
('ENG1036', 'Pooja Gupta', 'Broadband', 'North', 4, 'AVAILABLE', 0);

-- =============================================================================
-- 5. USER ACCOUNTS (passwords hashed with bcrypt - hash of "password123")
-- =============================================================================
-- Note: For demonstration, using bcrypt hash of "password123"
-- Real implementation should use proper password hashing
INSERT INTO user_account (username, password_hash, role, linked_id, status, failed_login_attempts) VALUES
-- Customers
('cust100245', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'CUSTOMER', 1, 'ACTIVE', 0),
('cust100246', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'CUSTOMER', 2, 'ACTIVE', 0),
('cust100247', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'CUSTOMER', 3, 'ACTIVE', 0),
-- Service Desk Administrators
('admin_sd1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'SERVICE_DESK_ADMIN', NULL, 'ACTIVE', 0),
('admin_sd2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'SERVICE_DESK_ADMIN', NULL, 'ACTIVE', 0),
-- Network Engineers
('eng1008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'NETWORK_ENGINEER', 1, 'ACTIVE', 0),
('eng1015', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'NETWORK_ENGINEER', 2, 'ACTIVE', 0),
('eng1021', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'NETWORK_ENGINEER', 3, 'ACTIVE', 0),
('eng1030', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'NETWORK_ENGINEER', 4, 'ACTIVE', 0),
-- Network Manager
('manager_nm1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/hu.e', 'NETWORK_MANAGER', NULL, 'ACTIVE', 0);

-- =============================================================================
-- 6. TROUBLE TICKET DATA (various statuses for testing)
-- =============================================================================
INSERT INTO trouble_ticket (ticket_number, customer_id, service_id, category, description, priority, severity, status, assigned_engineer_id, created_date, sla_deadline, resolution_date, root_cause, resolution_text, resolution_code) VALUES

-- CRITICAL ticket (IN_PROGRESS)
('TT-2026-004521', 1, 1, 'NETWORK_OUTAGE', 'Complete network outage at Mumbai enterprise site', 'CRITICAL', 'CRITICAL', 'IN_PROGRESS', 1, DATE_SUB(NOW(), INTERVAL 45 MINUTE), DATE_ADD(NOW(), INTERVAL 75 MINUTE), NULL, NULL, NULL, NULL),

-- HIGH priority tickets
('TT-2026-004522', 2, 3, 'SLOW_DATA', 'Broadband speed degradation - customer reporting 2Mbps instead of 10Mbps', 'HIGH', 'HIGH', 'ASSIGNED', 3, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_ADD(NOW(), INTERVAL 2 HOUR), NULL, NULL, NULL, NULL),
('TT-2026-004523', 3, 5, 'NO_CONNECTIVITY', 'VPN connection drops intermittently affecting remote workforce', 'HIGH', 'HIGH', 'ESCALATED', 2, DATE_SUB(NOW(), INTERVAL 1 HOUR), DATE_ADD(NOW(), INTERVAL 3 HOUR), NULL, NULL, NULL, NULL),

-- MEDIUM priority tickets
('TT-2026-004524', 4, 6, 'CALL_DROP', 'High call drop rate on enterprise mobile lines', 'MEDIUM', 'MEDIUM', 'IN_PROGRESS', 4, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_ADD(NOW(), INTERVAL 9 HOUR), NULL, NULL, NULL, NULL),
('TT-2026-004525', 5, 13, 'NETWORK_OUTAGE', 'Partial connectivity issues in Pune data center', 'MEDIUM', 'MEDIUM', 'OPEN', NULL, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_ADD(NOW(), INTERVAL 6 HOUR), NULL, NULL, NULL, NULL),
('TT-2026-004526', 6, 7, 'BILLING', 'Incorrect billing charges - overcharges detected', 'MEDIUM', 'LOW', 'OPEN', NULL, DATE_SUB(NOW(), INTERVAL 12 HOUR), DATE_ADD(NOW(), INTERVAL 0 HOUR), NULL, NULL, NULL, NULL),

-- LOW priority tickets
('TT-2026-004527', 7, 8, 'BROADBAND', 'Wi-Fi router needs configuration update', 'LOW', 'LOW', 'OPEN', NULL, DATE_SUB(NOW(), INTERVAL 24 HOUR), DATE_ADD(NOW(), INTERVAL 24 HOUR), NULL, NULL, NULL, NULL),
('TT-2026-004528', 8, 4, 'SIM_ISSUE', 'SIM card activation delay for new customer', 'LOW', 'LOW', 'ASSIGNED', 1, DATE_SUB(NOW(), INTERVAL 18 HOUR), DATE_ADD(NOW(), INTERVAL 30 HOUR), NULL, NULL, NULL, NULL),

-- RESOLVED tickets
('TT-2026-004519', 2, 4, 'CALL_DROP', 'Call drops on mobile line - intermittent issue', 'HIGH', 'MEDIUM', 'RESOLVED', 3, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY 20 HOUR), DATE_SUB(NOW(), INTERVAL 2 DAY 20 HOUR), 'Congestion on RAN cell', 'Capacity upgrade completed', 'CONFIGURATION_ERROR'),
('TT-2026-004520', 9, 14, 'SLOW_DATA', 'Mobile data speed extremely slow during peak hours', 'MEDIUM', 'MEDIUM', 'CLOSED', 2, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY 12 HOUR), DATE_SUB(NOW(), INTERVAL 4 DAY 10 HOUR), 'Network congestion', 'Deployed additional capacity', 'NETWORK_CONGESTION'),

-- CANCELLED ticket
('TT-2026-004530', 10, 15, 'OTHER', 'Customer changed mind - no longer required', 'LOW', 'LOW', 'CANCELLED', NULL, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, NULL, 'Customer request', 'Cancelled per customer', NULL),

-- PENDING_CUSTOMER
('TT-2026-004531', 1, 2, 'SIM_ISSUE', 'Duplicate SIM cards issue', 'MEDIUM', 'MEDIUM', 'PENDING_CUSTOMER', 1, DATE_SUB(NOW(), INTERVAL 6 HOUR), DATE_ADD(NOW(), INTERVAL 6 HOUR), NULL, 'Duplicate provisioning', 'Awaiting customer confirmation to proceed', NULL);

-- =============================================================================
-- 7. TICKET STATUS HISTORY
-- =============================================================================
INSERT INTO ticket_status_history (ticket_id, old_status, new_status, changed_by, changed_date, remarks) VALUES
(1, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 45 MINUTE), 'Assigned to Prakash Rao for critical issue'),
(1, 'ASSIGNED', 'IN_PROGRESS', 'eng1008', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'Engineer started troubleshooting'),
(2, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'Assigned to Rajesh Menon'),
(3, 'OPEN', 'ASSIGNED', 'admin_sd2', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'Assigned to Suresh Kumar'),
(3, 'ASSIGNED', 'ESCALATED', 'admin_sd2', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'Escalated due to SLA at risk'),
(4, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 3 HOUR), 'Assigned to Rohit Sharma'),
(4, 'ASSIGNED', 'IN_PROGRESS', 'eng1030', DATE_SUB(NOW(), INTERVAL 1 HOUR), 'Started investigation'),
(8, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 18 HOUR), 'Assigned to Prakash Rao'),
(9, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 3 DAY), 'Assigned to Rajesh Menon'),
(9, 'ASSIGNED', 'IN_PROGRESS', 'eng1021', DATE_SUB(NOW(), INTERVAL 3 DAY - INTERVAL 6 HOUR), 'Started work'),
(9, 'IN_PROGRESS', 'RESOLVED', 'eng1021', DATE_SUB(NOW(), INTERVAL 2 DAY 20 HOUR), 'Issue resolved'),
(9, 'RESOLVED', 'CLOSED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 2 DAY 10 HOUR), 'Ticket closed'),
(10, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 5 DAY), 'Assigned to Suresh Kumar'),
(10, 'ASSIGNED', 'IN_PROGRESS', 'eng1015', DATE_SUB(NOW(), INTERVAL 4 DAY 20 HOUR), 'Troubleshooting started'),
(10, 'IN_PROGRESS', 'RESOLVED', 'eng1015', DATE_SUB(NOW(), INTERVAL 4 DAY 10 HOUR), 'Resolved with capacity upgrade'),
(10, 'RESOLVED', 'CLOSED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 4 DAY 8 HOUR), 'Ticket closed'),
(11, 'OPEN', 'CANCELLED', 'admin_sd2', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'Cancelled per customer request'),
(12, 'OPEN', 'ASSIGNED', 'admin_sd1', DATE_SUB(NOW(), INTERVAL 6 HOUR), 'Assigned to Prakash Rao'),
(12, 'ASSIGNED', 'PENDING_CUSTOMER', 'eng1008', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'Awaiting customer confirmation');

-- =============================================================================
-- 8. ESCALATION HISTORY
-- =============================================================================
INSERT INTO escalation_history (ticket_id, from_level, to_level, reason, escalation_date, escalated_by) VALUES
(3, 'Engineer', 'Team Lead', 'SLA approaching breach - requires immediate attention', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 'admin_sd2');

-- =============================================================================
-- 9. LOGIN HISTORY
-- =============================================================================
INSERT INTO login_history (user_id, login_time, logout_time, ip_address, status) VALUES
(1, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 1 HOUR 45 MINUTE), '192.168.1.100', 'SUCCESS'),
(1, DATE_SUB(NOW(), INTERVAL 1 HOUR), NULL, '192.168.1.100', 'SUCCESS'),
(4, DATE_SUB(NOW(), INTERVAL 3 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR 30 MINUTE), '10.0.0.50', 'SUCCESS'),
(4, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, '10.0.0.50', 'SUCCESS'),
(6, DATE_SUB(NOW(), INTERVAL 4 HOUR), DATE_SUB(NOW(), INTERVAL 3 HOUR 15 MINUTE), '10.0.0.100', 'SUCCESS'),
(6, DATE_SUB(NOW(), INTERVAL 2 HOUR), NULL, '10.0.0.100', 'SUCCESS');

-- =============================================================================
-- 10. AUDIT LOG
-- =============================================================================
INSERT INTO audit_log (user_id, action, entity_type, entity_id, old_value, new_value, action_date) VALUES
('admin_sd1', 'CREATE', 'TICKET', 1, NULL, 'TT-2026-004521', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
('admin_sd1', 'UPDATE', 'TICKET', 1, 'status=OPEN', 'status=ASSIGNED', DATE_SUB(NOW(), INTERVAL 45 MINUTE)),
('eng1008', 'UPDATE', 'TICKET', 1, 'status=ASSIGNED', 'status=IN_PROGRESS', DATE_SUB(NOW(), INTERVAL 30 MINUTE)),
('admin_sd1', 'CREATE', 'TICKET', 2, NULL, 'TT-2026-004522', DATE_SUB(NOW(), INTERVAL 2 HOUR)),
('admin_sd2', 'CREATE', 'TICKET', 3, NULL, 'TT-2026-004523', DATE_SUB(NOW(), INTERVAL 1 HOUR));

-- =============================================================================
-- 11. NOTIFICATIONS
-- =============================================================================
INSERT INTO notification (recipient_id, ticket_id, message, notification_type, created_date, read_status) VALUES
('cust100245', 1, 'Your critical ticket TT-2026-004521 (Network Outage) has been assigned to Engineer Prakash Rao', 'ENGINEER_ASSIGNMENT', DATE_SUB(NOW(), INTERVAL 45 MINUTE), 1),
('cust100245', 1, 'ALERT: Your ticket TT-2026-004521 is at risk of SLA breach in 30 minutes', 'SLA_WARNING', DATE_SUB(NOW(), INTERVAL 15 MINUTE), 0),
('cust100246', 2, 'Your ticket TT-2026-004522 (Slow Data) has been assigned to Engineer Rajesh Menon', 'ENGINEER_ASSIGNMENT', DATE_SUB(NOW(), INTERVAL 2 HOUR), 1),
('eng1008', 1, 'You have been assigned ticket TT-2026-004521 - CRITICAL priority', 'TICKET_ASSIGNMENT', DATE_SUB(NOW(), INTERVAL 45 MINUTE), 1),
('manager_nm1', 3, 'ALERT: Ticket TT-2026-004523 has been escalated to Team Lead level', 'ESCALATION', DATE_SUB(NOW(), INTERVAL 30 MINUTE), 0),
('cust100248', 9, 'Your ticket TT-2026-004520 has been resolved. Thank you!', 'RESOLUTION', DATE_SUB(NOW(), INTERVAL 2 DAY 10 HOUR), 1);

-- =============================================================================
-- 12. FEEDBACK
-- =============================================================================
INSERT INTO feedback (ticket_id, customer_id, rating, comments, created_date) VALUES
(9, 2, 5, 'Excellent support! Issue resolved quickly and efficiently. Engineer was very professional.', DATE_SUB(NOW(), INTERVAL 2 DAY 8 HOUR)),
(10, 9, 4, 'Good response time and proper resolution. Could have been faster.', DATE_SUB(NOW(), INTERVAL 4 DAY 6 HOUR));

-- =============================================================================
-- COMMIT TRANSACTION
-- =============================================================================
COMMIT;
