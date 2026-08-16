# Telecom Service Assurance & Trouble Ticket Management System (TSATMS)

A complete Java 8 console application for managing telecom customer incidents, trouble tickets, and service assurance with role-based dashboards.

## System Overview

**TSATMS** is a sophisticated incident management system built with:
- **Java 8** with Lambda, Stream API, and modern Java features
- **MySQL 8.x** database with fully normalized schema
- **JDBC** for database access (no ORM frameworks)
- **Console UI** for 4 different user roles
- **Advanced Features**: Multithreading, Observer pattern, PriorityQueue, SLA tracking, escalation management

### Core Features

1. **Authentication System**
   - Multi-factor authentication: CAPTCHA → Password → OTP
   - Account locking after repeated failures
   - Login history tracking
   - Role-based access control

2. **Ticket Management**
   - Trouble ticket lifecycle from creation to resolution
   - Automatic SLA deadline calculation
   - Status tracking with complete audit trail
   - Ticket escalation (4-level hierarchy)
   - Engineer assignment with recommendation engine

3. **SLA Management**
   - Configurable SLAs by priority level
   - Automatic status calculation (WITHIN_SLA, AT_RISK, BREACHED)
   - SLA monitoring and alerts

4. **Engineer Management**
   - Engineer skill-based assignment using Stream API + Lambda
   - Workload balancing and availability tracking
   - Regional and specialization-based filtering

5. **Reporting**
   - Stream API-based report generation
   - CSV/TXT export functionality
   - Various reports: SLA Compliance, Engineer Performance, Incident Analysis

6. **Multithreading**
   - Network event processor (background thread)
   - SLA monitor
   - Notification processor
   - Report generator

## Prerequisites

- **Java 8 or higher** (must be Java 8 due to Lambda expressions)
- **Maven 3.6+**
- **MySQL Server 8.0+**
- **Git** (optional, for cloning)

## Installation & Setup

### Step 1: Set Up MySQL Database

```bash
# Start MySQL server (if not already running)
# On Windows: 
mysql -u root -p

# On Linux/Mac:
mysql -u root -p
```

### Step 2: Create Database and Schema

```bash
# Run the schema script
mysql -u root -p < database/schema.sql

# Run the seed data script
mysql -u root -p < database/seed_data.sql
```

**Verify database creation:**
```bash
mysql -u root -p
> USE tsatms_db;
> SHOW TABLES;
```

You should see 13 tables created.

### Step 3: Clone or Extract Project

```bash
# If using git
git clone <repository-url>
cd telecom-tsatms

# Or extract the zip file
unzip telecom-tsatms.zip
cd telecom-tsatms
```

### Step 4: Build with Maven

```bash
# Clean and compile
mvn clean compile

# Build the project
mvn clean package

# Expected output: BUILD SUCCESS
```

### Step 5: Verify Compilation

```bash
# Check that the JAR was created
ls -la target/
# You should see: telecom-tsatms-1.0.0.jar
```

## Running the Application

### Option 1: Run using Maven (Recommended)

```bash
mvn exec:java -Dexec.mainClass="com.amdocs.telecom.main.Application"
```

### Option 2: Run the JAR directly

```bash
java -jar target/telecom-tsatms-1.0.0-jar-with-dependencies.jar
```

### Option 3: Run the Fat JAR (with all dependencies)

```bash
# Build with assembly plugin
mvn clean package assembly:single

# Run it
java -jar target/telecom-tsatms-1.0.0-jar-with-dependencies.jar
```

## Test Login Credentials

**Note:** All passwords are: `password123`

### Customer Role
- **Username:** `cust100245`
- **Password:** `password123`
- **Customer ID:** 1 (Rajesh Kumar - Enterprise)

### Service Desk Administrator
- **Username:** `admin_sd1`
- **Password:** `password123`

### Network Engineer
- **Username:** `eng1008`
- **Password:** `password123`
- **Name:** Prakash Rao
- **Specialization:** Core Network

### Network Manager
- **Username:** `manager_nm1`
- **Password:** `password123`

### Demo CAPTCHA & OTP

When testing:
- **CAPTCHA:** The system will display a 6-character code. For demo, enter the same code shown.
- **OTP:** The system will display a 6-digit code. For demo, enter the same code shown.

## Database Configuration

Default configuration in `DBConnection.java`:
```java
DB_URL = "jdbc:mysql://localhost:3306/tsatms_db?useSSL=false&serverTimezone=UTC"
DB_USER = "root"
DB_PASSWORD = "root"
```

**To change database credentials:**
1. Edit: `src/main/java/com/amdocs/telecom/util/DBConnection.java`
2. Update `DB_USER` and `DB_PASSWORD` constants
3. Rebuild: `mvn clean package`

## Project Structure

```
telecom-tsatms/
├── src/main/java/com/amdocs/telecom/
│   ├── main/
│   │   └── Application.java          (Entry point)
│   ├── controller/                    (Console UI controllers)
│   ├── service/                       (Business logic)
│   │   ├── impl/
│   │   └── AuthenticationService
│   ├── dao/                           (Data Access Objects)
│   │   ├── impl/
│   │   ├── CustomerDAO
│   │   ├── NetworkEngineerDAO
│   │   └── TroubleTicketDAO
│   ├── model/                         (Entity models)
│   │   ├── Customer
│   │   ├── TroubleTicket
│   │   ├── NetworkEngineer
│   │   └── ... (all entities)
│   ├── dto/                           (Data Transfer Objects)
│   ├── security/                      (Security utilities)
│   │   ├── PasswordUtil
│   │   ├── CaptchaGenerator
│   │   └── OTPService
│   ├── exception/                     (Custom exceptions)
│   ├── scheduler/                     (Multithreading)
│   ├── report/                        (Report generation)
│   └── util/                          (Utilities)
│       ├── DBConnection
│       └── DateUtil
├── database/
│   ├── schema.sql                     (DDL - Table definitions)
│   └── seed_data.sql                  (Sample data)
├── pom.xml                            (Maven configuration)
└── README.md
```

## Key Features Demonstrated

### 1. Java 8 Features
- **Lambda Expressions:** Used in Stream API for ticket filtering and engineer selection
- **Stream API:** Aggregations, filtering, grouping for reports
- **Optional:** Proper null handling without `.get()` without presence checks
- **Method References:** Function composition and callback handling
- **Default Interface Methods:** Utility methods in DAO interfaces

### 2. JDBC Features
- **PreparedStatement:** All SQL queries use prepared statements (SQL injection prevention)
- **Transactions:** Multi-step operations with commit/rollback capability
- **Batch Processing:** Bulk inserts/updates when needed
- **Connection Pooling:** Singleton DBConnection pattern
- **Exception Handling:** Wrapped exceptions to business layer

### 3. Design Patterns
- **DAO Pattern:** Complete separation of business logic from database access
- **Singleton:** DBConnection for centralized connection management
- **Factory:** Service factory for creating appropriate service instances
- **Strategy:** Engineer assignment strategy based on multiple criteria
- **Observer:** Notification system triggered by ticket status changes

### 4. Security
- **Password Hashing:** BCrypt for secure password storage
- **CAPTCHA:** Simple alphanumeric challenge for bot prevention
- **OTP:** Time-limited one-time password for 2FA
- **Account Locking:** Auto-lock after failed attempts
- **Audit Logging:** Track all user actions

### 5. Multithreading
- **ExecutorService:** Thread pool for parallel operations
- **ScheduledExecutorService:** Periodic tasks (SLA monitoring)
- **BlockingQueue:** Producer-consumer for network events
- **Synchronization:** Thread-safe operations on shared state
- **Callable/Future:** Asynchronous result handling

## Running Tests

Verify the authentication system:
```bash
# Start application
mvn exec:java -Dexec.mainClass="com.amdocs.telecom.main.Application"

# Try login with:
# Role: Customer Login
# Username: cust100245
# Password: password123
# CAPTCHA: (enter the code shown)
# OTP: (enter the code shown)
```

## Database Schema Highlights

### Core Tables
- **customer:** Customer information and type
- **telecom_service:** Services subscribed by customers
- **network_engineer:** Engineer skills and availability
- **trouble_ticket:** Main ticket entity with SLA tracking
- **ticket_status_history:** Complete audit trail
- **escalation_history:** Escalation tracking
- **sla_configuration:** SLA rules by priority

### Supporting Tables
- **user_account:** Authentication credentials and roles
- **login_history:** Login attempts and success tracking
- **notification:** User notifications
- **network_event:** Network monitoring events
- **audit_log:** System action audit trail
- **feedback:** Customer satisfaction ratings

## SQL Features Used

- **Views:** `vw_open_tickets`, `vw_sla_compliance`, `vw_engineer_workload`
- **Indexes:** Performance optimization on frequently queried columns
- **Joins:** Multi-table queries with proper relationships
- **Subqueries:** Complex filtering and aggregation
- **Aggregation Functions:** COUNT, SUM, AVG, MAX
- **Date Functions:** Date arithmetic for SLA calculations
- **GROUP BY/HAVING:** Report generation

## Troubleshooting

### Database Connection Issues
```
ERROR: Failed to establish database connection
SOLUTION: 
1. Ensure MySQL is running
2. Check credentials in DBConnection.java
3. Verify database exists: mysql -u root -p -e "SHOW DATABASES;"
```

### Maven Build Failures
```
ERROR: [ERROR] COMPILATION ERROR
SOLUTION:
1. Ensure Java 8: java -version
2. Clean rebuild: mvn clean install
3. Check all dependencies are downloaded
```

### JDBC Driver Not Found
```
ERROR: MySQL JDBC Driver not found
SOLUTION:
1. Maven should auto-download. If not:
   mvn dependency:resolve
2. Check ~/.m2/repository/mysql/mysql-connector-java/8.0.33/
```

### Port Already in Use
```
ERROR: Port 3306 already in use
SOLUTION:
1. Change MySQL port in my.cnf
2. Or: netstat -an | grep 3306 (find and kill process)
```

## Next Steps / Future Enhancements

The current implementation provides a solid foundation. Future enhancements could include:

1. **Web UI:** Convert to Spring Boot REST API with Angular/React frontend
2. **Real SMS/Email:** Integrate Twilio/SendGrid for actual OTP delivery
3. **Advanced Analytics:** Machine learning for ticket routing optimization
4. **Mobile App:** React Native or Flutter mobile application
5. **Real-time Notifications:** WebSocket-based real-time updates
6. **Performance Metrics:** More sophisticated SLA tracking and forecasting
7. **Integration APIs:** Third-party system integrations

## Performance Considerations

- **Database Indexing:** Strategic indexes on frequently queried columns
- **Connection Pooling:** Singleton pattern prevents connection leaks
- **Stream Processing:** Efficient in-memory filtering vs SQL queries
- **Pagination:** Implemented for large result sets
- **Batch Operations:** Bulk inserts for better throughput

## Support & Documentation

For detailed information about:
- **Architecture:** See design documentation
- **API Reference:** Javadoc comments in source code
- **Database Schema:** See `database/schema.sql` comments
- **Test Cases:** See test directory

## License

This is a demonstration project for educational purposes.

---

**Version:** 1.0.0  
**Last Updated:** August 2026  
**Author:** Telecom Engineering Team
