# Telecom Service Assurance & Trouble Ticket Management System (TSATMS)
## Complete User & Administrator Operating Manual

---

## 📖 Executive Summary

The **Telecom Service Assurance & Trouble Ticket Management System (TSATMS)** is an enterprise-grade Java 8 console application designed to manage telecom customer incidents, trouble ticket lifecycles, service assurance, network event monitoring, and SLA compliance.

This manual provides complete operational guidelines for running, configuring, and handling all 4 user roles in the system.

---

## 🛠️ 1. System Requirements & Setup Guide

### 1.1 Prerequisites
- **Java Development Kit (JDK):** Version 8 or higher (`java -version`)
- **Build Tool:** Apache Maven 3.6+ (`mvn -version`)
- **Database Server:** MySQL Server 8.0+ (`mysql --version`)

### 1.2 Database Initialization
Open your terminal and run the DDL and DML scripts to initialize the database:

```cmd
d:
cd \Krishna
mysql -u root -p < database\schema.sql
mysql -u root -p < database\seed_data.sql
```
> **Database Password:** Enter `krishna` when prompted.

### 1.3 Database Connection Settings
Database credentials are managed in `src/main/java/com/amdocs/telecom/util/DBConnection.java`:

```java
DB_URL = "jdbc:mysql://localhost:3306/tsatms_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
DB_USER = "root";
DB_PASSWORD = "krishna";
```

### 1.4 Compilation & Launch Commands
To build the package:
```cmd
mvn clean package
```

To start the application:
```cmd
mvn exec:java -Dexec.mainClass="com.amdocs.telecom.main.Application"
```

---

## 🔐 2. Authentication & Security Flow

Every user login follows a **Three-Factor Authentication (3FA)** sequence:

```
┌─────────────────┐     ┌───────────────────┐     ┌────────────────┐     ┌───────────────────┐
│ 1. Choose Role  │ ──> │ 2. Verify CAPTCHA │ ──> │ 3. Credentials │ ──> │ 4. Verify 2FA OTP │
└─────────────────┘     └───────────────────┘     └────────────────┘     └───────────────────┘
```

1. **Step 1 - CAPTCHA Verification:** A 6-character alphanumeric code is generated. Enter the code shown on screen.
2. **Step 2 - Credentials Verification:** Enter your role username and password (`password123`).
3. **Step 3 - OTP Verification:** A 6-digit one-time password is generated. Enter the 6-digit code shown on screen.
4. **Step 4 - Role Dashboard Routing:** Upon validation, you are directed to your role-specific interactive dashboard.

---

## 👤 3. Role-Based Operating Manual

### 🔑 Reference Test Accounts
All test accounts use the password: `password123`

| Role | Username | Associated Profile / ID | Description |
|---|---|---|---|
| **Customer** | `cust100245` | Customer ID 1 (Rajesh Kumar - Enterprise) | Manages enterprise telecom services and raises tickets |
| **Service Desk Admin** | `admin_sd1` | Service Desk Team | Manages open tickets, engineer assignment, SLA, and reports |
| **Network Engineer** | `eng1008` | Engineer ID 1 (Prakash Rao - Core Network) | Resolves assigned technical issues and updates work logs |
| **Network Manager** | `manager_nm1` | Network Operations Lead | Oversees metrics, engineer performance, and escalations |

---

### 3.1 Customer Operating Guide (Role 1)

Upon logging in as a **Customer** (`cust100245` / `password123`), you have access to the following 8 operations:

```
========== CUSTOMER DASHBOARD ==========
1. View My Services
2. Raise Trouble Ticket
3. View My Tickets
4. Track Ticket
5. View Ticket History
6. View Notifications
7. Submit Feedback
8. Logout
```

#### Detailed Operations:
1. **View My Services (`Option 1`):** Displays all telecom services subscribed by your account (e.g., Enterprise Connectivity, Mobile Postpaid, Broadband) along with their active statuses.
2. **Raise Trouble Ticket (`Option 2`):**
   - Step 1: Select which subscribed service has an issue.
   - Step 2: Input Incident Category (e.g., `NETWORK_OUTAGE`, `SLOW_DATA`, `CALL_DROP`, `BROADBAND`).
   - Step 3: Enter detailed description of the fault.
   - Step 4: Select Priority (`1. LOW`, `2. MEDIUM`, `3. HIGH`, `4. CRITICAL`).
   - *Result:* The system automatically calculates the SLA target resolution deadline and generates a unique ticket number (e.g. `TKT482910`).
3. **View My Tickets (`Option 3`):** Displays a summary table of all tickets you have submitted.
4. **Track Ticket (`Option 4`):** Enter a specific ticket number (e.g. `TKT-2026-004521`) to view real-time status, assigned engineer, and resolution progress.
5. **View Notifications (`Option 6`):** Displays automated notifications sent to your account (e.g., engineer assignment updates, ticket resolution alerts).
6. **Submit Feedback (`Option 7`):** Enter ticket ID, rating (1 to 5 stars), and feedback comments once your ticket is resolved.

---

### 3.2 Service Desk Administrator Guide (Role 2)

Logging in as **Service Desk Administrator** (`admin_sd1` / `password123`) grants full operational control over open incidents:

```
========== SERVICE_DESK_ADMIN DASHBOARD ==========
1. View Open Tickets
2. Assign Engineer
3. Reassign Ticket
4. Escalate Ticket
5. Update Priority
6. Monitor SLA
7. Close Ticket
8. Generate Reports
9. Logout
```

#### Detailed Operations:
1. **View Open Tickets (`Option 1`):** Renders a master summary table listing ticket ID, ticket number, customer name, priority, current status, SLA status (`WITHIN_SLA`, `AT_RISK`, `BREACHED`), and assigned engineer.
2. **Assign Engineer (`Option 2`):**
   - **Mode 1 - Automatic Recommendation Engine (Java 8 Stream API + Lambda):** Filters available engineers matching region/specialization and automatically assigns the engineer with the lowest active workload and highest experience.
   - **Mode 2 - Manual Assignment:** Lists all network engineers with active ticket counts and lets you manually assign a specific engineer ID.
3. **Escalate Ticket (`Option 4`):** Escalates a ticket to higher authority levels (`1. TEAM_LEAD`, `2. NETWORK_MANAGER`, `3. OPERATIONS_MANAGER`) with an audit reason.
4. **Update Priority (`Option 5`):** Changes ticket priority (e.g. from `MEDIUM` to `CRITICAL`), which automatically recalculates the SLA deadline based on `sla_configuration`.
5. **Monitor SLA (`Option 6`):** Executes real-time SLA audit across all open tickets, flagging at-risk or breached tickets and dispatching alerts.
6. **Close Ticket (`Option 7`):** Closes a resolved ticket with closure remarks.
7. **Generate Reports (`Option 8`):** Generates reports for:
   - **SLA Compliance Report**
   - **Engineer Performance Report**
   - **Incident Analysis Report**
   - Supports export formats: `CONSOLE` (rendered on screen), `TXT`, or `CSV` (exported into `reports/` folder).

---

### 3.3 Network Engineer Operating Guide (Role 3)

Logging in as a **Network Engineer** (`eng1008` / `password123`) opens your technical work environment:

```
========== NETWORK_ENGINEER DASHBOARD ==========
1. View Assigned Tickets
2. Update Ticket Status
3. Add Resolution
4. View Ticket Details
5. Check SLA Status
6. Logout
```

#### Detailed Operations:
1. **View Assigned Tickets (`Option 1`):** Lists all active trouble tickets assigned to your employee code, sorted by priority and SLA deadline.
2. **Update Ticket Status (`Option 2`):** Change ticket status to `IN_PROGRESS` when starting work, or `PENDING_CUSTOMER` if waiting for customer input.
3. **Add Resolution (`Option 3`):**
   - Input Root Cause Analysis text.
   - Input Resolution Description text.
   - Select Resolution Code (`HARDWARE_FAILURE`, `CONFIGURATION_ERROR`, `NETWORK_CONGESTION`, `SOFTWARE_FAILURE`, `FIBER_CUT`, `POWER_FAILURE`).
   - *Result:* Sets ticket status to `RESOLVED`, logs completion timestamp, and automatically decrements your active ticket count.
4. **Check SLA Status (`Option 5`):** Displays remaining SLA minutes for each of your assigned tickets to prevent SLA breaches.

---

### 3.4 Network Manager Operating Guide (Role 4)

Logging in as **Network Manager** (`manager_nm1` / `password123`) provides high-level executive analytics:

```
========== NETWORK_MANAGER DASHBOARD ==========
1. View Dashboard Metrics
2. View Engineer Performance
3. Generate SLA Report
4. Generate Incident Report
5. Manage Escalations
6. Logout
```

#### Detailed Operations:
1. **View Dashboard Metrics (`Option 1`):** Displays high-level KPIs:
   - Total Open Trouble Tickets
   - Critical Incidents Count
   - SLA At Risk Count (deadline within 30 mins)
   - SLA Breached Count
   - Total vs Available Engineers Count
2. **View Engineer Performance (`Option 2`):** Generates a comparative table of engineer specializations, active ticket workloads, critical ticket counts, and availability states.
3. **Manage Escalations (`Option 5`):** View all tickets marked as `ESCALATED` and reassign them directly to senior network specialists.

---

## ⚡ 4. Background Multithreaded Services

The application runs autonomous background threads upon startup:

1. **SLA Monitor Scheduler (`SLAMonitorScheduler.java`):**
   - Runs a single-thread scheduled executor every 30 seconds.
   - Audits all open tickets against `sla_deadline`.
   - Generates `SLA_WARNING` notifications for tickets expiring in <30 minutes and `SLA_BREACH` alerts for expired tickets.
2. **Network Event Processor (`NetworkEventProcessor.java`):**
   - Consumes network monitoring events from a `BlockingQueue<NetworkEvent>`.
   - Automatically raises a `CRITICAL` trouble ticket when a `CRITICAL` severity network outage event is detected.
3. **Notification Processor (`NotificationProcessor.java`):**
   - Asynchronously logs and dispatches system notifications without blocking the user interface.

---

## 📊 5. Troubleshooting Reference

| Symptom | Cause | Solution |
|---|---|---|
| `Non-parseable POM` error | XML syntax error in `pom.xml` | Already resolved (escaped `&amp;` in name tag) |
| `MissingProjectException` | Running Maven in wrong folder | Run `cd \Krishna` before executing `mvn clean package` |
| `Database connection is not available` | MySQL service stopped or wrong credentials | Ensure MySQL is running on port 3306 and password in `DBConnection.java` is `krishna` |
| `Invalid password` error | Incorrect password entered | Use `password123` for all test user accounts |

---

**TSATMS Version:** 1.0.0  
**Java Version:** 1.8+  
**Build Tool:** Maven 3.6+  
**Database:** MySQL 8.0+
