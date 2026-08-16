-- =============================================================================
-- TELECOM SERVICE ASSURANCE & TROUBLE TICKET MANAGEMENT SYSTEM (TSATMS)
-- Complete Normalized Relational Database Schema (MySQL 8.x)
-- =============================================================================

-- Drop existing database and recreate
DROP DATABASE IF EXISTS tsatms_db;
CREATE DATABASE tsatms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tsatms_db;

-- =============================================================================
-- 1. CUSTOMER TABLE
-- =============================================================================
CREATE TABLE customer (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_number VARCHAR(20) NOT NULL UNIQUE,
    customer_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    mobile_number VARCHAR(15) NOT NULL UNIQUE,
    customer_type ENUM('CONSUMER', 'SME', 'ENTERPRISE') NOT NULL,
    city VARCHAR(50) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_customer_number (customer_number),
    INDEX idx_email (email),
    INDEX idx_status (status),
    INDEX idx_created_date (created_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 2. TELECOM_SERVICE TABLE
-- =============================================================================
CREATE TABLE telecom_service (
    service_id INT PRIMARY KEY AUTO_INCREMENT,
    service_code VARCHAR(30) NOT NULL UNIQUE,
    service_name VARCHAR(100) NOT NULL,
    service_type ENUM('MOBILE', 'BROADBAND', 'ENTERPRISE_CONNECTIVITY', 'VPN', 'CLOUD_CONNECTIVITY') NOT NULL,
    customer_id INT NOT NULL,
    activation_date DATE NOT NULL,
    service_status ENUM('ACTIVE', 'INACTIVE', 'SUSPENDED') NOT NULL DEFAULT 'ACTIVE',
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_service_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    INDEX idx_service_code (service_code),
    INDEX idx_customer_id (customer_id),
    INDEX idx_service_type (service_type),
    INDEX idx_service_status (service_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 3. NETWORK_ENGINEER TABLE
-- =============================================================================
CREATE TABLE network_engineer (
    engineer_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_code VARCHAR(20) NOT NULL UNIQUE,
    engineer_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    experience_years INT NOT NULL,
    availability ENUM('AVAILABLE', 'BUSY', 'ON_LEAVE', 'OFFLINE') NOT NULL DEFAULT 'AVAILABLE',
    active_ticket_count INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_employee_code (employee_code),
    INDEX idx_region (region),
    INDEX idx_specialization (specialization),
    INDEX idx_availability (availability)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 4. SLA_CONFIGURATION TABLE
-- =============================================================================
CREATE TABLE sla_configuration (
    sla_config_id INT PRIMARY KEY AUTO_INCREMENT,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL UNIQUE,
    response_sla_minutes INT NOT NULL,
    resolution_sla_hours INT NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_priority (priority)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 5. TROUBLE_TICKET TABLE (Main ticket entity)
-- =============================================================================
CREATE TABLE trouble_ticket (
    ticket_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    service_id INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    priority ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    severity VARCHAR(50) NOT NULL,
    status ENUM('OPEN', 'ASSIGNED', 'IN_PROGRESS', 'PENDING_CUSTOMER', 'ESCALATED', 'RESOLVED', 'CLOSED', 'CANCELLED') NOT NULL DEFAULT 'OPEN',
    assigned_engineer_id INT,
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    sla_deadline DATETIME,
    resolution_date DATETIME,
    root_cause TEXT,
    resolution_text TEXT,
    resolution_code VARCHAR(50),
    CONSTRAINT fk_ticket_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_service FOREIGN KEY (service_id) REFERENCES telecom_service(service_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_engineer FOREIGN KEY (assigned_engineer_id) REFERENCES network_engineer(engineer_id) ON DELETE SET NULL,
    INDEX idx_ticket_number (ticket_number),
    INDEX idx_customer_id (customer_id),
    INDEX idx_service_id (service_id),
    INDEX idx_assigned_engineer_id (assigned_engineer_id),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_created_date (created_date),
    INDEX idx_sla_deadline (sla_deadline),
    INDEX idx_resolution_date (resolution_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 6. TICKET_STATUS_HISTORY TABLE
-- =============================================================================
CREATE TABLE ticket_status_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    old_status VARCHAR(50) NOT NULL,
    new_status VARCHAR(50) NOT NULL,
    changed_by VARCHAR(100) NOT NULL,
    changed_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    remarks TEXT,
    CONSTRAINT fk_history_ticket FOREIGN KEY (ticket_id) REFERENCES trouble_ticket(ticket_id) ON DELETE CASCADE,
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_changed_date (changed_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 7. ESCALATION_HISTORY TABLE
-- =============================================================================
CREATE TABLE escalation_history (
    escalation_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    from_level VARCHAR(50) NOT NULL,
    to_level VARCHAR(50) NOT NULL,
    reason TEXT NOT NULL,
    escalation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    escalated_by VARCHAR(100) NOT NULL,
    CONSTRAINT fk_escalation_ticket FOREIGN KEY (ticket_id) REFERENCES trouble_ticket(ticket_id) ON DELETE CASCADE,
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_escalation_date (escalation_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 8. NETWORK_EVENT TABLE
-- =============================================================================
CREATE TABLE network_event (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    event_number VARCHAR(20) NOT NULL UNIQUE,
    network_node VARCHAR(100) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    severity ENUM('LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    event_time DATETIME NOT NULL,
    ticket_created_id INT,
    processed INT NOT NULL DEFAULT 0,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_event_ticket FOREIGN KEY (ticket_created_id) REFERENCES trouble_ticket(ticket_id) ON DELETE SET NULL,
    INDEX idx_event_number (event_number),
    INDEX idx_network_node (network_node),
    INDEX idx_severity (severity),
    INDEX idx_processed (processed),
    INDEX idx_event_time (event_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 9. NOTIFICATION TABLE
-- =============================================================================
CREATE TABLE notification (
    notification_id INT PRIMARY KEY AUTO_INCREMENT,
    recipient_id VARCHAR(100) NOT NULL,
    ticket_id INT,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_status INT NOT NULL DEFAULT 0,
    INDEX idx_recipient_id (recipient_id),
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_read_status (read_status),
    INDEX idx_created_date (created_date),
    CONSTRAINT fk_notification_ticket FOREIGN KEY (ticket_id) REFERENCES trouble_ticket(ticket_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 10. USER_ACCOUNT TABLE (for login/authentication)
-- =============================================================================
CREATE TABLE user_account (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role ENUM('CUSTOMER', 'SERVICE_DESK_ADMIN', 'NETWORK_ENGINEER', 'NETWORK_MANAGER') NOT NULL,
    linked_id INT,
    status ENUM('ACTIVE', 'LOCKED', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    failed_login_attempts INT NOT NULL DEFAULT 0,
    lock_until DATETIME,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 11. LOGIN_HISTORY TABLE
-- =============================================================================
CREATE TABLE login_history (
    login_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time DATETIME,
    ip_address VARCHAR(50),
    status ENUM('SUCCESS', 'FAILED', 'LOCKED') NOT NULL,
    CONSTRAINT fk_login_user FOREIGN KEY (user_id) REFERENCES user_account(user_id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 12. AUDIT_LOG TABLE
-- =============================================================================
CREATE TABLE audit_log (
    audit_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id INT,
    old_value TEXT,
    new_value TEXT,
    action_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_entity_type (entity_type),
    INDEX idx_action_date (action_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- 13. FEEDBACK TABLE
-- =============================================================================
CREATE TABLE feedback (
    feedback_id INT PRIMARY KEY AUTO_INCREMENT,
    ticket_id INT NOT NULL,
    customer_id INT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comments TEXT,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_feedback_ticket FOREIGN KEY (ticket_id) REFERENCES trouble_ticket(ticket_id) ON DELETE CASCADE,
    CONSTRAINT fk_feedback_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    INDEX idx_ticket_id (ticket_id),
    INDEX idx_customer_id (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================================================
-- VIEWS FOR REPORTING AND ANALYSIS
-- =============================================================================

-- View: Open Tickets with Customer and Service Details
CREATE VIEW vw_open_tickets AS
SELECT 
    tt.ticket_id,
    tt.ticket_number,
    c.customer_name,
    c.customer_number,
    ts.service_name,
    ts.service_type,
    tt.category,
    tt.priority,
    tt.severity,
    tt.status,
    tt.created_date,
    tt.sla_deadline,
    ne.engineer_name,
    CASE 
        WHEN tt.sla_deadline < NOW() THEN 'BREACHED'
        WHEN tt.sla_deadline < DATE_ADD(NOW(), INTERVAL 30 MINUTE) THEN 'AT_RISK'
        ELSE 'WITHIN_SLA'
    END AS sla_status
FROM trouble_ticket tt
JOIN customer c ON tt.customer_id = c.customer_id
JOIN telecom_service ts ON tt.service_id = ts.service_id
LEFT JOIN network_engineer ne ON tt.assigned_engineer_id = ne.engineer_id
WHERE tt.status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED');

-- View: SLA Compliance Summary
CREATE VIEW vw_sla_compliance AS
SELECT 
    tt.ticket_id,
    tt.ticket_number,
    tt.priority,
    tt.created_date,
    tt.resolution_date,
    tt.sla_deadline,
    CASE 
        WHEN tt.status IN ('RESOLVED', 'CLOSED') AND tt.resolution_date <= tt.sla_deadline THEN 'COMPLIANT'
        WHEN tt.status IN ('RESOLVED', 'CLOSED') AND tt.resolution_date > tt.sla_deadline THEN 'BREACHED'
        WHEN tt.sla_deadline < NOW() THEN 'BREACHED'
        ELSE 'ON_TRACK'
    END AS sla_status
FROM trouble_ticket tt;

-- View: Engineer Workload Summary
CREATE VIEW vw_engineer_workload AS
SELECT 
    ne.engineer_id,
    ne.engineer_name,
    ne.specialization,
    ne.region,
    ne.experience_years,
    ne.availability,
    COUNT(CASE WHEN tt.status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') THEN 1 END) AS active_tickets,
    COUNT(CASE WHEN tt.priority = 'CRITICAL' AND tt.status NOT IN ('RESOLVED', 'CLOSED', 'CANCELLED') THEN 1 END) AS critical_tickets
FROM network_engineer ne
LEFT JOIN trouble_ticket tt ON ne.engineer_id = tt.assigned_engineer_id
GROUP BY ne.engineer_id, ne.engineer_name, ne.specialization, ne.region, ne.experience_years, ne.availability;

-- View: Incident Analysis by Category
CREATE VIEW vw_incident_analysis AS
SELECT 
    tt.category,
    COUNT(*) AS total_incidents,
    COUNT(CASE WHEN tt.priority = 'CRITICAL' THEN 1 END) AS critical_count,
    COUNT(CASE WHEN tt.status IN ('RESOLVED', 'CLOSED') THEN 1 END) AS resolved_count,
    AVG(EXTRACT(EPOCH FROM (COALESCE(tt.resolution_date, NOW()) - tt.created_date)) / 3600) AS avg_resolution_hours
FROM trouble_ticket tt
GROUP BY tt.category;

-- =============================================================================
-- INDEXES FOR PERFORMANCE
-- =============================================================================
CREATE INDEX idx_ticket_priority_status ON trouble_ticket(priority, status);
CREATE INDEX idx_ticket_created_priority ON trouble_ticket(created_date DESC, priority);
CREATE INDEX idx_engineer_region_specialization ON network_engineer(region, specialization);
CREATE INDEX idx_service_customer_status ON telecom_service(customer_id, service_status);
CREATE INDEX idx_notification_recipient_read ON notification(recipient_id, read_status);

-- =============================================================================
-- PRIVILEGES AND SETUP COMPLETE
-- =============================================================================
COMMIT;
