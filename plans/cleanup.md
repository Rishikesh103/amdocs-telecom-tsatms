# TSATMS — Deep Code Quality Cleanup Plan
**Standard:** Java 8 best practices · No new features · Rewrite only
**Naming:** `PascalCase` classes/enums · `camelCase` methods/variables · `UPPER_SNAKE_CASE` constants

---

## Audit Summary

| Category | Issues Found | Severity |
|---|---|---|
| Magic literals (hard-coded strings/numbers) | 8 | HIGH |
| Naming convention violations | 5 | HIGH |
| Resource management / GC issues | 4 | HIGH |
| Non-volatile shared mutable flag | 1 | HIGH |
| Swallowed / silent exception | 1 | MEDIUM |
| Mutable fields that should be `final` | 2 | MEDIUM |
| Bare `System.out` / `System.err` calls | 1 | MEDIUM |
| Dead code (unused field + dead methods) | 3 | MEDIUM |
| Wildcard imports | 6 files | LOW |
| `printStackTrace()` calls | 1 | LOW |

---

## HIGH — Fix First

---

### ISSUE-01 · Magic Literals — TroubleTicketServiceImpl.java
Lines: 58, 100, 228, 269, 297, 328, 367, 430

PROBLEM: Hard-coded numbers and strings scattered across the file.

  BEFORE L58:
    String ticketNum = "TKT" + (100000 + new Random().nextInt(900000));

  BEFORE L100, 228, 269, 297, 328, 367, 430:
    notifCust.setRecipientId("cust" + (100244 + customerId));

Rule: Magic literals make the code fragile and unreadable. Extract to named constants.

  FIX — add class-level constants (UPPER_SNAKE_CASE):
    private static final String TICKET_PREFIX       = "TKT";
    private static final int    TICKET_NUMBER_MIN   = 100000;
    private static final int    TICKET_NUMBER_RANGE = 900000;
    private static final String CUSTOMER_ID_PREFIX  = "cust";
    private static final int    CUSTOMER_ID_OFFSET  = 100244;
    private static final Random TICKET_RANDOM       = new Random(); // static — not per-call

  FIX — usage at L58:
    String ticketNum = TICKET_PREFIX + (TICKET_NUMBER_MIN + TICKET_RANDOM.nextInt(TICKET_NUMBER_RANGE));

  FIX — usage at L100, 228, 269, 297, 328, 367, 430:
    notifCust.setRecipientId(CUSTOMER_ID_PREFIX + (CUSTOMER_ID_OFFSET + customerId));

NOTE: new Random() inside a method creates a new RNG instance every call — wasteful and bad for GC.
Make it private static final Random at class level.

---

### ISSUE-02 · Naming Violation — NetworkEngineer.engineerName
File: model/NetworkEngineer.java
Field: private String engineerName

PROBLEM: The class is named NetworkEngineer. The field engineerName repeats the class name — an anti-pattern.
"engineer.getEngineerName()" reads redundantly.

  BEFORE:
    private String engineerName;
    public String getEngineerName() { return engineerName; }
    public void setEngineerName(String engineerName) { this.engineerName = engineerName; }

  AFTER:
    private String fullName;
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

Update all callers:
  - NetworkEngineerDAOImpl.java  (mapper + create SQL)
  - TroubleTicketServiceImpl.java  L215, 258
  - ManagerServiceImpl.java  L70
  - ServiceDeskController.java  L177, 190
  - ManagerController.java  L92, 197

---

### ISSUE-03 · Naming Violation — Customer.customerName
File: model/Customer.java
Field: private String customerName

Same problem as ISSUE-02 — repeats class name in field name.

  BEFORE:  private String customerName;
  AFTER:   private String fullName;

Update all callers:
  - CustomerDAOImpl.java  (mapper)
  - TroubleTicketServiceImpl.java  L138
  - ServiceDeskController.java  L93

---

### ISSUE-04 · Naming Violation — Loop variable `e` (ambiguous)
Files: ServiceDeskController.java L188, ManagerController.java L155 L195

PROBLEM: Loop variable named `e` looks like an Exception variable — confusing.

  BEFORE:
    for (NetworkEngineer e : engineers) { ... e.getEngineerName() ... }

  AFTER:
    for (NetworkEngineer engineer : engineers) { ... engineer.getFullName() ... }

---

### ISSUE-05 · Non-volatile Shared Flag — SLAMonitorScheduler.running
File: scheduler/SLAMonitorScheduler.java  Line: 14

PROBLEM: private boolean running = false is accessed from multiple threads (caller + scheduler thread)
but is NOT volatile. JVM may cache the value — stop() may never be visible to the scheduler thread.
This is a concurrency correctness bug.

  BEFORE:  private boolean running = false;
  AFTER:   private volatile boolean running = false;

---

### ISSUE-06 · GC Issue — new Random() per method call
File: service/impl/TroubleTicketServiceImpl.java  Line: 58

PROBLEM: new Random() is called inside createTicket() — a new RNG object is allocated every time a
ticket is created. This is wasteful and unnecessary for the GC.

  BEFORE:
    String ticketNum = "TKT" + (100000 + new Random().nextInt(900000));

  AFTER (add to class):
    private static final Random TICKET_RANDOM = new Random();

  ...then use TICKET_RANDOM.nextInt(TICKET_NUMBER_RANGE) at L58.

(Covered also by ISSUE-01 — both fixes apply together.)

---

### ISSUE-07 · Swallowed Exception — NetworkEventProcessor.java
File: scheduler/NetworkEventProcessor.java  Lines: 63-65

PROBLEM: Outer catch (Exception e) in run() loop swallows all exceptions silently.
Real bugs (NullPointerException, DB errors) are completely invisible.

  BEFORE:
    } catch (Exception e) {
        // Keep background thread alive
    }

  AFTER:
    } catch (Exception e) {
        System.err.println("[ERROR] [NetworkEventProcessor] Unexpected error in event loop: " + e.getMessage());
        // Keep background thread alive — do not re-throw
    }

---

## MEDIUM — Fix After High Priority

---

### ISSUE-08 · Dead Code — DBConnection orphaned field and methods
File: util/DBConnection.java  Lines: 16, 75-98

PROBLEM:
  - private Connection connection — field declared but never used.
    getConnection() creates fresh connections via DriverManager each time.
  - closeConnection() — references the orphaned field; calls e.printStackTrace() inside
  - isConnectionActive() — references the orphaned field; always returns false (field is always null)
  - connect() private method — populates the orphaned field; also dead

  FIX: Remove all of:
    - private Connection connection;  (L16)
    - connect() method
    - closeConnection() method
    - isConnectionActive() method
  
  Clean constructor:
    private DBConnection() {
        // Fresh connection obtained per-call via getConnection()
    }

  Also remove the Class.forName() call from getConnection() — DriverManager auto-loads the driver
  in JDBC 4.0+ (Java 6+). Can keep it but it is technically redundant.

---

### ISSUE-09 · Dead Call — Application.java closeConnection()
File: main/Application.java  Line: 79

PROBLEM: Calls DBConnection.getInstance().closeConnection() in finally block.
This method will be deleted in ISSUE-08. Also — connections are opened/closed per-call
in each DAO using try-with-resources so there is nothing to close here.

  FIX: Remove line 79 entirely:
    // DELETE: DBConnection.getInstance().closeConnection();
  
  Replace with comment:
    // Connections closed per-call via try-with-resources in each DAO

---

### ISSUE-10 · printStackTrace() — Application.java
File: main/Application.java  Line: 67

PROBLEM: e.printStackTrace() dumps raw stack to stderr with no context.
Bypasses ConsoleUI output channel.

  BEFORE:
    } catch (Exception e) {
        ConsoleUI.printError("Fatal System Error: " + e.getMessage());
        e.printStackTrace();
    }

  AFTER:
    } catch (Exception e) {
        ConsoleUI.printError("Fatal System Error: " + e.getMessage());
        System.err.println("[FATAL] Cause: " + e.getClass().getSimpleName() + " — " + e.getMessage());
    }

---

### ISSUE-11 · Bare System.out.println — AuthenticationService.logout()
File: service/AuthenticationService.java  Line: 204

PROBLEM: logout() contains a bare System.out.println bypassing the ConsoleUI design system.
The surrounding try/catch (Exception e) is also dead code — the body cannot throw anything.

  BEFORE:
    public void logout(int userId) throws AuthenticationException {
        try {
            System.out.println("[SYSTEM] User " + userId + " logged out successfully.");
        } catch (Exception e) {
            throw new AuthenticationException("Error logging out: " + e.getMessage(), e);
        }
    }

  AFTER:
    public void logout(int userId) {
        // Logout acknowledgment handled by controller layer via ConsoleUI
        // No server-side action needed in this single-user simulation
    }

---

### ISSUE-12 · Mutable Fields Not Declared final — Service classes
Files: All service/impl/*.java

PROBLEM: DAO fields injected in constructor are never reassigned but not declared final.
Violates immutability best practice. Makes classes open to accidental mutation.

  BEFORE (example TroubleTicketServiceImpl):
    private TroubleTicketDAO ticketDAO;
    private CustomerDAO customerDAO;
    // ...9 fields, none final

  AFTER:
    private final TroubleTicketDAO ticketDAO;
    private final CustomerDAO customerDAO;
    // ...all 9 fields final

Affected files and fields to make final:
  TroubleTicketServiceImpl.java    — all 9 DAO fields
  ManagerServiceImpl.java          — ticketDAO, engineerDAO
  SLAMonitorServiceImpl.java       — ticketDAO, notificationDAO
  ReportServiceImpl.java           — ticketDAO, engineerDAO
  EngineerServiceImpl.java         — all DAO fields
  CustomerServiceImpl.java         — all DAO fields
  AuthenticationService.java       — userAccountDAO, loginHistoryDAO

---

### ISSUE-13 · Mutable field — AuthenticationService DAOs not final
File: service/AuthenticationService.java  Lines: 33-34

PROBLEM: userAccountDAO and loginHistoryDAO are assigned in constructor and never changed,
but are not declared final.

  BEFORE:
    private UserAccountDAO userAccountDAO;
    private LoginHistoryDAO loginHistoryDAO;

  AFTER:
    private final UserAccountDAO userAccountDAO;
    private final LoginHistoryDAO loginHistoryDAO;

(Covered also by ISSUE-12)

---

### ISSUE-14 · mkdirs() return value ignored — ReportServiceImpl.java
File: service/impl/ReportServiceImpl.java  Line: 33

PROBLEM: dir.mkdirs() returns false if directory creation fails (permissions, disk full).
Return value is silently ignored.

  BEFORE:
    dir.mkdirs();

  AFTER:
    if (!dir.exists() && !dir.mkdirs()) {
        ConsoleUI.printError("Could not create reports directory. File export may fail.");
    }

---

### ISSUE-15 · Bare System.out.println — SLAMonitorScheduler.java
File: scheduler/SLAMonitorScheduler.java  Lines: 35, 50

PROBLEM: Uses bare System.out.println for startup/shutdown messages instead of consistent format.

  BEFORE:
    System.out.println("? SLA Monitor Scheduler started (runs every 30 seconds).");

  AFTER:
    System.out.println("[INFO ] [SLAMonitorScheduler] Started — scanning every 30 seconds.");

---

### ISSUE-16 · Bare System.out.println — NetworkEventProcessor.java
File: scheduler/NetworkEventProcessor.java  Lines: 32, 82

  BEFORE:
    System.out.println("? Network Event Processor Thread started.");

  AFTER:
    System.out.println("[INFO ] [NetworkEventProcessor] Thread started — consuming event queue.");

---

## LOW — Polish Pass

---

### ISSUE-17 · Missing @Override annotations
Files: service/impl/CustomerServiceImpl.java, EngineerServiceImpl.java

PROBLEM: Some service implementations omit @Override on interface methods.
@Override lets the compiler catch signature mismatches — important for correctness.

  FIX: Add @Override before every method that implements an interface method.
  Most DAO impls already have this correctly.

---

### ISSUE-18 · Inconsistent Null Guard — SLAMonitorServiceImpl.java
File: service/impl/SLAMonitorServiceImpl.java  Line: 50

PROBLEM: dto.setCategory(t.getCategory()) — getCategory() returns String which could be null.
No null guard present.

  BEFORE:
    dto.setCategory(t.getCategory());

  AFTER:
    dto.setCategory(t.getCategory() != null ? t.getCategory() : "UNKNOWN");

---

### ISSUE-19 · Wildcard Imports
Files: TroubleTicketServiceImpl, ManagerServiceImpl, SLAMonitorServiceImpl, 
       ReportServiceImpl, ServiceDeskController, EngineerController

PROBLEM: Wildcard imports (import com.amdocs.telecom.dao.*;) hide what is actually used,
make analysis harder, and can cause name collisions.

  BEFORE:
    import com.amdocs.telecom.dao.*;
    import com.amdocs.telecom.dao.impl.*;
    import com.amdocs.telecom.model.*;

  AFTER: Replace with explicit individual imports only.

---

## Execution Order

Execute in 4 groups. Build and verify after each group.

GROUP A — Safest, no ripple effects (do first):
  1. ISSUE-05 — Add volatile to SLAMonitorScheduler.running
  2. ISSUE-07 — Fix swallowed exception in NetworkEventProcessor
  3. ISSUE-10 — Replace printStackTrace() in Application
  4. ISSUE-12 — Add final to all DAO fields in service impls
  5. ISSUE-14 — Fix mkdirs() ignored return
  6. ISSUE-15, ISSUE-16 — Consistent log format in schedulers

  BUILD -> confirm BUILD SUCCESS

GROUP B — Constants (rename within single files):
  7. ISSUE-01 + ISSUE-06 — Extract magic literals + static Random in TroubleTicketServiceImpl
  8. ISSUE-08 + ISSUE-09 — Remove dead code in DBConnection + Application
  9. ISSUE-11 — Clean up logout() in AuthenticationService

  BUILD -> confirm BUILD SUCCESS

GROUP C — Renames (highest blast radius — update all callers):
  10. ISSUE-02 — Rename engineerName -> fullName in NetworkEngineer + all callers
  11. ISSUE-03 — Rename customerName -> fullName in Customer + all callers
  12. ISSUE-04 — Rename loop variables e -> engineer where ambiguous

  BUILD -> confirm BUILD SUCCESS

GROUP D — Low-risk polish:
  13. ISSUE-17 — Add missing @Override annotations
  14. ISSUE-18 — Add null guard for category in SLA audit
  15. ISSUE-19 — Replace wildcard imports with explicit imports

  FINAL BUILD -> confirm BUILD SUCCESS -> ready for commit

---

## Build Command
```
Get-Process -Name java -ErrorAction SilentlyContinue | Stop-Process -Force -ErrorAction SilentlyContinue;
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-8.0.502.7-hotspot";
$env:PATH = "$env:JAVA_HOME\bin;C:\maven\bin;$env:PATH";
& "C:\maven\bin\mvn.cmd" package -DskipTests
```

---

## Files Changed Summary

| File | Issues | Group |
|---|---|---|
| scheduler/SLAMonitorScheduler.java | ISSUE-05, ISSUE-15 | A |
| scheduler/NetworkEventProcessor.java | ISSUE-07, ISSUE-16 | A |
| main/Application.java | ISSUE-09, ISSUE-10 | A + B |
| service/impl/TroubleTicketServiceImpl.java | ISSUE-01, ISSUE-06, ISSUE-12 | A + B |
| service/impl/ManagerServiceImpl.java | ISSUE-12 | A |
| service/impl/SLAMonitorServiceImpl.java | ISSUE-12, ISSUE-18 | A |
| service/impl/ReportServiceImpl.java | ISSUE-12, ISSUE-14 | A |
| service/impl/EngineerServiceImpl.java | ISSUE-12 | A |
| service/impl/CustomerServiceImpl.java | ISSUE-12, ISSUE-17 | A |
| service/AuthenticationService.java | ISSUE-11, ISSUE-13 | B |
| util/DBConnection.java | ISSUE-08 | B |
| model/NetworkEngineer.java | ISSUE-02 | C |
| model/Customer.java | ISSUE-03 | C |
| dao/impl/NetworkEngineerDAOImpl.java | ISSUE-02, ISSUE-04 | C |
| controller/ServiceDeskController.java | ISSUE-04 | C |
| controller/ManagerController.java | ISSUE-04 | C |
| Multiple files | ISSUE-17, ISSUE-19 | D |
