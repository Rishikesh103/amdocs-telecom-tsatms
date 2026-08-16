package com.amdocs.telecom.main;

import com.amdocs.telecom.controller.*;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.scheduler.NetworkEventProcessor;
import com.amdocs.telecom.scheduler.SLAMonitorScheduler;
import com.amdocs.telecom.service.AuthenticationService;
import com.amdocs.telecom.util.ConsoleUI;
import com.amdocs.telecom.util.DBConnection;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Main application entry point for TSATMS (Telecom Service Assurance & Trouble Ticket Management System).
 * Cyberpunk NOC & Mission Control Console Edition
 */
public class Application {
    
    private static Scanner scanner;
    private static AuthenticationService authService;
    private static UserAccount currentUser;
    
    // Background schedulers
    private static SLAMonitorScheduler slaScheduler;
    private static NetworkEventProcessor eventProcessor;

    // Controllers
    private static CustomerController customerController;
    private static ServiceDeskController serviceDeskController;
    private static EngineerController engineerController;
    private static ManagerController managerController;
    
    public static void main(String[] args) {
        try {
            ConsoleUI.printMainBanner();
            
            // Initialize components
            scanner = new Scanner(System.in);
            authService = new AuthenticationService();
            
            // Initialize controllers
            customerController = new CustomerController();
            serviceDeskController = new ServiceDeskController();
            engineerController = new EngineerController();
            managerController = new ManagerController();

            // Start background thread schedulers
            try {
                slaScheduler = new SLAMonitorScheduler();
                slaScheduler.start();

                eventProcessor = new NetworkEventProcessor();
                eventProcessor.start();
                
                System.out.println(ConsoleUI.BRIGHT_GREEN + "  ● [DAEMON] Real-Time SLA Monitor Online (Tick: 30s)" + ConsoleUI.RESET);
                System.out.println(ConsoleUI.BRIGHT_GREEN + "  ● [DAEMON] Event Processing Engine Active (BlockingQueue)" + ConsoleUI.RESET);
                System.out.println(ConsoleUI.DIM + "  ─────────────────────────────────────────────────────────────" + ConsoleUI.RESET);
            } catch (Exception e) {
                ConsoleUI.printInfo("Background services initialized.");
            }
            
            // Show main menu
            showMainMenu();
            
        } catch (Exception e) {
            ConsoleUI.printError("Fatal System Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (slaScheduler != null) {
                slaScheduler.stop();
            }
            if (eventProcessor != null) {
                eventProcessor.stop();
            }
            if (scanner != null) {
                scanner.close();
            }
            // Close database connection
            DBConnection.getInstance().closeConnection();
        }
    }
    
    /**
     * Displays the main menu and handles user selection.
     */
    private static void showMainMenu() {
        boolean running = true;
        
        while (running) {
            ConsoleUI.printHeader("NOC SECURE ACCESS GATEWAY", "Select authentication profile to proceed");
            
            ConsoleUI.printMenuOption("1", "Customer Self-Service Portal", "👤");
            ConsoleUI.printMenuOption("2", "Service Desk Operations Console", "🎧");
            ConsoleUI.printMenuOption("3", "Network Engineer Workbench", "⚡");
            ConsoleUI.printMenuOption("4", "Executive Manager Telemetry", "📊");
            ConsoleUI.printMenuOption("5", "Terminate Session & Exit", "🔴");
            
            ConsoleUI.printPrompt("Select Portal (1-5)");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    attemptLogin("CUSTOMER");
                    break;
                case "2":
                    attemptLogin("SERVICE_DESK_ADMIN");
                    break;
                case "3":
                    attemptLogin("NETWORK_ENGINEER");
                    break;
                case "4":
                    attemptLogin("NETWORK_MANAGER");
                    break;
                case "5":
                    System.out.println("\n" + ConsoleUI.BRIGHT_MAGENTA + "  ┌─────────────────────────────────────────────────────────┐");
                    System.out.println("  │   Thank you for using TSATMS Assurance System. Bye!     │");
                    System.out.println("  └─────────────────────────────────────────────────────────┘" + ConsoleUI.RESET);
                    running = false;
                    break;
                default:
                    ConsoleUI.printError("Invalid portal selection. Please choose 1 - 5.");
            }
        }
    }
    
    /**
     * Attempts to authenticate a user through the login process.
     * Flow: CAPTCHA → Credentials → OTP → Role Dashboard
     */
    private static void attemptLogin(String roleName) {
        try {
            ConsoleUI.printHeader(roleName + " AUTHENTICATION", "Multi-Factor Zero-Trust Verification");
            
            // Step 1: Start login and show CAPTCHA
            ConsoleUI.printSection("STEP 1/3: BOT MITIGATION & CAPTCHA");
            String captchaCode = authService.startLogin();
            
            System.out.println("  " + ConsoleUI.BG_DARK_GRAY + ConsoleUI.BRIGHT_YELLOW + ConsoleUI.BOLD + 
                    " 🔐 CAPTCHA TOKEN: [ " + captchaCode + " ] " + ConsoleUI.RESET);
            
            ConsoleUI.printPrompt("Enter CAPTCHA Verification Code");
            String captchaResponse = scanner.nextLine().trim();
            
            if (!authService.validateCaptcha(captchaResponse)) {
                ConsoleUI.printError("CAPTCHA verification failed! Authentication aborted.");
                return;
            }
            ConsoleUI.printSuccess("CAPTCHA verified successfully.");
            
            // Step 2: Username and Password
            ConsoleUI.printSection("STEP 2/3: CREDENTIALS CHALLENGE");
            ConsoleUI.printPrompt("Username");
            String username = scanner.nextLine().trim();
            ConsoleUI.printPrompt("Password");
            String password = scanner.nextLine().trim();
            
            if (!authService.verifyCredentials(username, password)) {
                ConsoleUI.printError("Access Denied: Invalid credentials provided.");
                return;
            }
            ConsoleUI.printSuccess("Credentials validated against BCrypt security database.");
            
            // Step 3: OTP Verification
            ConsoleUI.printSection("STEP 3/3: TWO-FACTOR OTP VERIFICATION");
            String otp = authService.getOTPForDisplay();
            System.out.println("  " + ConsoleUI.BG_BLUE + ConsoleUI.BRIGHT_WHITE + ConsoleUI.BOLD + 
                    " 📲 OTP DISPATCHED: [ " + otp + " ] " + ConsoleUI.RESET + ConsoleUI.DIM + " (Simulated SMS/Email Gateway)" + ConsoleUI.RESET);
            System.out.println(ConsoleUI.DIM + "  Attempts Remaining: " + authService.getOTPRemainingAttempts() + ConsoleUI.RESET);
            
            ConsoleUI.printPrompt("Enter 6-Digit OTP");
            String otpResponse = scanner.nextLine().trim();
            
            if (!authService.validateOTP(otpResponse)) {
                ConsoleUI.printError("OTP challenge failed! Remaining attempts: " + authService.getOTPRemainingAttempts());
                return;
            }
            ConsoleUI.printSuccess("Identity Verified! Zero-Trust Handshake Complete.");
            
            // Step 4: Complete login
            currentUser = authService.completeLogin(username);
            
            ConsoleUI.printCard("ACTIVE SESSION GRANTED", Arrays.asList(
                    "Operator   : " + currentUser.getUsername(),
                    "Role       : " + currentUser.getRole().getDescription(),
                    "Privilege  : " + currentUser.getRole().name(),
                    "Status     : " + ConsoleUI.BRIGHT_GREEN + "AUTHENTICATED ●" + ConsoleUI.RESET
            ), ConsoleUI.BRIGHT_CYAN);
            
            // Show role-based dashboard
            showRoleDashboard(currentUser.getRole().name());
            
        } catch (AuthenticationException e) {
            ConsoleUI.printError("Authentication Exception: " + e.getMessage());
        }
    }
    
    /**
     * Displays role-specific dashboard and delegates choice handling to controllers.
     */
    private static void showRoleDashboard(String role) {
        boolean inDashboard = true;
        
        while (inDashboard) {
            switch (role) {
                case "CUSTOMER":
                    inDashboard = showCustomerDashboard();
                    break;
                case "SERVICE_DESK_ADMIN":
                    inDashboard = showServiceDeskDashboard();
                    break;
                case "NETWORK_ENGINEER":
                    inDashboard = showEngineerDashboard();
                    break;
                case "NETWORK_MANAGER":
                    inDashboard = showManagerDashboard();
                    break;
                default:
                    inDashboard = false;
            }
        }
    }
    
    /**
     * Customer Dashboard
     */
    private static boolean showCustomerDashboard() {
        ConsoleUI.printHeader("CUSTOMER SELF-SERVICE PORTAL", "Logged in as: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View My Subscribed Services", "📱");
        ConsoleUI.printMenuOption("2", "Raise New Trouble Ticket", "🎫");
        ConsoleUI.printMenuOption("3", "View My Active Tickets", "📋");
        ConsoleUI.printMenuOption("4", "Track Ticket Real-Time Status", "🔍");
        ConsoleUI.printMenuOption("5", "View Complete Ticket Audit History", "📜");
        ConsoleUI.printMenuOption("6", "View Push Notifications", "🔔");
        ConsoleUI.printMenuOption("7", "Submit Service Feedback & Rating", "⭐");
        ConsoleUI.printMenuOption("8", "Secure Sign Out", "🚪");
        
        ConsoleUI.printPrompt("Enter Action (1-8)");
        String choice = scanner.nextLine().trim();
        return customerController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Service Desk Administrator Dashboard
     */
    private static boolean showServiceDeskDashboard() {
        ConsoleUI.printHeader("SERVICE DESK OPERATIONS COMMAND", "Operator: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View Active Trouble Tickets Queue", "📋");
        ConsoleUI.printMenuOption("2", "Assign Engineer (AI-Auto / Manual)", "🤖");
        ConsoleUI.printMenuOption("3", "Reassign Ticket Workload", "🔄");
        ConsoleUI.printMenuOption("4", "Escalate Incident (Hierarchical)", "🚨");
        ConsoleUI.printMenuOption("5", "Update Priority & Recalculate SLA", "⚡");
        ConsoleUI.printMenuOption("6", "Run Live Real-Time SLA Audit", "⏱️");
        ConsoleUI.printMenuOption("7", "Verify & Close Ticket", "🔒");
        ConsoleUI.printMenuOption("8", "Generate Analytical Reports", "📈");
        ConsoleUI.printMenuOption("9", "Secure Sign Out", "🚪");
        
        ConsoleUI.printPrompt("Enter Action (1-9)");
        String choice = scanner.nextLine().trim();
        return serviceDeskController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Network Engineer Dashboard
     */
    private static boolean showEngineerDashboard() {
        ConsoleUI.printHeader("NETWORK ENGINEER WORKBENCH", "Engineer: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View My Assigned Incident Queue", "🔧");
        ConsoleUI.printMenuOption("2", "Update Ticket Workflow Status", "🔄");
        ConsoleUI.printMenuOption("3", "Submit Resolution & Root Cause", "✔");
        ConsoleUI.printMenuOption("4", "Inspect Ticket Deep Telemetry", "🔍");
        ConsoleUI.printMenuOption("5", "Check My SLA Deadlines & Risk", "⏱️");
        ConsoleUI.printMenuOption("6", "Secure Sign Out", "🚪");
        
        ConsoleUI.printPrompt("Enter Action (1-6)");
        String choice = scanner.nextLine().trim();
        return engineerController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Network Manager Dashboard
     */
    private static boolean showManagerDashboard() {
        ConsoleUI.printHeader("EXECUTIVE TELEMETRY & NOC METRICS", "Manager: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "Executive Health Dashboard & KPIs", "📊");
        ConsoleUI.printMenuOption("2", "Engineer Workload & Performance Matrix", "👥");
        ConsoleUI.printMenuOption("3", "Generate SLA Compliance Audit (CSV/TXT)", "📑");
        ConsoleUI.printMenuOption("4", "Generate Incident Root-Cause Analysis", "📈");
        ConsoleUI.printMenuOption("5", "Manage Escalation Command Queue", "🚨");
        ConsoleUI.printMenuOption("6", "Secure Sign Out", "🚪");
        
        ConsoleUI.printPrompt("Enter Action (1-6)");
        String choice = scanner.nextLine().trim();
        return managerController.handleMenuChoice(choice, currentUser, scanner);
    }
}
