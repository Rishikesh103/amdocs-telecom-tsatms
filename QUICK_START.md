# TSATMS Quick Start Guide

## ⚡ Get Started in 5 Minutes

### Prerequisites Check
```bash
# Verify Java 8+
java -version
# Expected: java version "1.8.0" or higher

# Verify Maven
mvn -version
# Expected: Apache Maven 3.6.0 or higher

# Verify MySQL
mysql --version
# Expected: mysql Ver 8.0 or higher
```

---

## 🔧 Setup Steps

### Step 1: Database Setup (2 minutes)
```bash
# Open MySQL
mysql -u root -p

# Copy-paste these SQL commands:
CREATE DATABASE tsatms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tsatms_db;

# Run schema (copy all content from database/schema.sql)
# Run seed data (copy all content from database/seed_data.sql)

# Verify
SHOW TABLES;
SELECT COUNT(*) FROM customer;
```

**Expected Output:**
```
+----------------------------+
| Tables_in_tsatms_db        |
+----------------------------+
| customer                   |
| telecom_service            |
| network_engineer           |
| trouble_ticket             |
| (+ 9 more tables)          |
+----------------------------+
13 rows in set

COUNT(*): 10
```

### Step 2: Build Project (2 minutes)
```bash
cd d:\Krishna

# Clean and compile
mvn clean compile

# Build complete package
mvn clean package
```

**Expected Output:**
```
[INFO] Building jar: .../telecom-tsatms-1.0.0.jar
[INFO] BUILD SUCCESS
```

### Step 3: Run Application (1 minute)
```bash
# Run via Maven
mvn exec:java -Dexec.mainClass="com.amdocs.telecom.main.Application"

# OR run JAR directly
java -jar target/telecom-tsatms-1.0.0.jar
```

**Expected Output:**
```
========================================================
TELECOM SERVICE ASSURANCE & TROUBLE TICKET MANAGEMENT
========================================================

========== MAIN MENU ==========
1. Customer Login
2. Service Desk Login
3. Network Engineer Login
4. Network Manager Login
5. Exit

Enter your choice: 
```

---

## 🧪 Test Login Walkthrough

### Customer Login Test
```
1. At main menu, enter: 1
2. At CAPTCHA prompt, enter: (copy the displayed code)
   Example: If shown "ABC123", type ABC123
3. At username prompt, enter: cust100245
4. At password prompt, enter: password123
5. At OTP prompt, enter: (copy the displayed code)
   Example: If shown "123456", type 123456
6. You should see "✓ LOGIN SUCCESSFUL" and Customer Dashboard

Expected result:
Customer Dashboard with options:
- View My Services
- Raise Trouble Ticket
- Track Ticket
- etc.
```

### Other Test Credentials
```
SERVICE DESK:
- Username: admin_sd1
- Password: password123

ENGINEER:
- Username: eng1008
- Password: password123

MANAGER:
- Username: manager_nm1
- Password: password123
```

---

## 📁 Project Structure
```
d:\Krishna\
├── src/main/java/com/amdocs/telecom/
│   ├── main/
│   │   └── Application.java          ← Entry point
│   ├── model/                        ← All 13 entity classes
│   ├── dao/                          ← DAO interfaces + implementations
│   ├── service/                      ← Business logic
│   ├── security/                     ← Password, CAPTCHA, OTP
│   ├── exception/                    ← Custom exceptions
│   ├── util/                         ← DBConnection, DateUtil
│   └── ...
├── database/
│   ├── schema.sql                    ← Table definitions
│   └── seed_data.sql                 ← Sample data
├── pom.xml                           ← Maven config
├── README.md                         ← Full documentation
├── IMPLEMENTATION_SUMMARY.md         ← What was built
└── QUICK_START.md                    ← This file
```

---

## 🔍 Troubleshooting Quick Fixes

### "Cannot connect to MySQL"
```bash
# Start MySQL service
net start MySQL80
# (Windows) or: brew services start mysql (Mac)

# Verify MySQL is running
mysql -u root -p -e "SELECT 1;"
```

### "Database tables not found"
```bash
# Re-create tables
mysql -u root -p tsatms_db < database/schema.sql
mysql -u root -p tsatms_db < database/seed_data.sql
```

### "Build fails with compilation errors"
```bash
# Clean and retry
mvn clean install
mvn compile
```

### "Login fails with 'User not found'"
```
# Verify test data loaded
mysql -u root -p tsatms_db -e "SELECT * FROM user_account LIMIT 5;"

# Should show rows with users like: cust100245, admin_sd1, eng1008
```

---

## 📊 What You Can Do

After login, explore:

1. **Customer Role**
   - View services
   - Track tickets
   - Submit feedback

2. **Service Desk**
   - View open tickets
   - Assign engineers
   - Monitor SLA

3. **Engineer**
   - View assigned tickets
   - Update ticket status
   - Add resolution notes

4. **Manager**
   - View dashboard metrics
   - Generate reports
   - Monitor SLA compliance

---

## 🎯 Next Steps

### Feature Implementation
Add functionality to:
- Customer dashboard endpoints
- Ticket creation workflow
- Engineer assignment algorithm
- SLA monitoring
- Report generation

### Enhancement Ideas
- [ ] Web REST API using Spring Boot
- [ ] React/Angular frontend
- [ ] Real SMS/Email for OTP
- [ ] Real-time notifications (WebSocket)
- [ ] Performance analytics
- [ ] Mobile app

---

## 📚 Documentation

| File | Purpose |
|------|---------|
| README.md | Full setup and configuration guide |
| IMPLEMENTATION_SUMMARY.md | Complete component list |
| Source Code Comments | Javadoc and inline explanations |
| database/schema.sql | Database design documentation |

---

## ⚙️ Default Configuration

**Database:**
- Host: localhost
- Port: 3306
- Database: tsatms_db
- User: root
- Password: root

**Application:**
- Entry Point: com.amdocs.telecom.main.Application
- Java Version: 1.8+
- Build Tool: Maven 3.6+

**Security:**
- Password Hashing: BCrypt (strength 12)
- Account Lock Duration: 30 minutes
- Max Login Attempts: 5
- CAPTCHA Expiry: 5 minutes
- OTP Expiry: 10 minutes
- OTP Max Attempts: 3

---

## ✅ Verification Checklist

- [ ] Java 8+ installed
- [ ] Maven 3.6+ installed
- [ ] MySQL 8.0+ running
- [ ] Database created
- [ ] Schema tables created
- [ ] Seed data inserted
- [ ] Project compiles without errors
- [ ] Application starts successfully
- [ ] Login flow works (CAPTCHA → Password → OTP)
- [ ] Customer dashboard appears

---

## 🆘 Need Help?

1. **Check README.md** - Full troubleshooting section
2. **Check IMPLEMENTATION_SUMMARY.md** - Architecture and design
3. **Database Setup** - See database/schema.sql comments
4. **Login Issues** - Verify credentials in seed_data.sql

---

**Ready? Start with Step 1: Database Setup! ⬆️**

Good luck! 🚀
