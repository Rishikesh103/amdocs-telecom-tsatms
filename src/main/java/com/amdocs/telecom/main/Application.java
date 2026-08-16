package com.amdocs.telecom.main;

import com.amdocs.telecom.controller.CustomerController;
import com.amdocs.telecom.controller.EngineerController;
import com.amdocs.telecom.controller.ManagerController;
import com.amdocs.telecom.controller.ServiceDeskController;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.scheduler.NetworkEventProcessor;
import com.amdocs.telecom.scheduler.SLAMonitorScheduler;
import com.amdocs.telecom.service.AuthenticationService;
import com.amdocs.telecom.util.ConsoleUI;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Main Application Entry Point
 * Amdocs TSATMS - Telecom Service Assurance & Incident Management System
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
                
                ConsoleUI.printInfo("Daemon services active: SLA Monitoring Scheduler (30s tick), Network Event Processor");
            } catch (Exception e) {
                ConsoleUI.printInfo("Background services initialized.");
            }
            
            // Show main menu
            showMainMenu();
            
        } catch (Exception e) {
            ConsoleUI.printError("Fatal System Error: " + e.getMessage());
            System.err.println("[FATAL] Cause: " + e.getClass().getSimpleName() + " — " + e.getMessage());
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
            // Connections closed per-call via try-with-resources in each DAO
        }
    }
    
    /**
     * Displays the main menu and handles user selection.
     */
    private static void showMainMenu() {
        boolean running = true;
        
        while (running) {
            ConsoleUI.printHeader("Main Access Gateway", "Select role profile to sign in");
            
            ConsoleUI.printMenuOption("1", "Customer Self-Service Portal");
            ConsoleUI.printMenuOption("2", "Service Desk Operations Console");
            ConsoleUI.printMenuOption("3", "Network Engineer Workbench");
            ConsoleUI.printMenuOption("4", "Network Operations Manager Dashboard");
            ConsoleUI.printMenuOption("5", "Forgot Password / Reset Password");
            ConsoleUI.printMenuOption("6", "Exit System");
            
            ConsoleUI.printPrompt("Select option (1-6)");
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
                    handleForgotPassword();
                    break;
                case "6":
                case "0":
                    System.out.println("\n" + ConsoleUI.CYAN + "Session closed. Thank you for using Amdocs TSATMS." + ConsoleUI.RESET);
                    running = false;
                    break;
                default:
                    ConsoleUI.printError("Invalid choice. Please select from 1 to 6 (or 0 to Exit).");
            }
        }
    }

    private static void handleForgotPassword() {
        try {
            ConsoleUI.printHeader("Forgot Password / Account Recovery", "Secure OTP Password Reset");
            ConsoleUI.printPrompt("Enter your Username (or 0 to Cancel)");
            String username = scanner.nextLine().trim();
            if ("0".equals(username) || "back".equalsIgnoreCase(username) || username.isEmpty()) {
                ConsoleUI.printInfo("Password recovery canceled.");
                return;
            }

            authService.requestPasswordReset(username);
            String otp = authService.getOTPForDisplay();
            ConsoleUI.printSection("Two-Factor OTP Security Challenge");
            System.out.println("  One-Time Password (OTP): " + ConsoleUI.BOLD + otp + ConsoleUI.RESET + " [Simulated SMS/Email Gateway]");
            System.out.println(ConsoleUI.DIM + "  Attempts remaining: " + authService.getOTPRemainingAttempts() + ConsoleUI.RESET);

            ConsoleUI.printPrompt("Enter 6-digit OTP (or 0 to Cancel)");
            String enteredOtp = scanner.nextLine().trim();
            if ("0".equals(enteredOtp) || "back".equalsIgnoreCase(enteredOtp)) {
                ConsoleUI.printInfo("Password recovery canceled.");
                return;
            }

            if (!authService.validateOTP(enteredOtp)) {
                ConsoleUI.printError("OTP verification failed. Remaining attempts: " + authService.getOTPRemainingAttempts());
                return;
            }
            ConsoleUI.printSuccess("Identity verified.");

            ConsoleUI.printPrompt("Enter New Password");
            String newPassword = scanner.nextLine().trim();
            authService.resetPassword(username, newPassword);
            ConsoleUI.printSuccess("Password has been reset successfully! You can now log in with your new password.");

        } catch (Exception e) {
            ConsoleUI.printError("Password Reset Error: " + e.getMessage());
        }
    }
    
    /**
     * Attempts to authenticate a user through the login process.
     * Flow: CAPTCHA -> Credentials -> OTP -> Role Dashboard
     */
    private static void attemptLogin(String roleName) {
        try {
            ConsoleUI.printHeader(roleName.replace("_", " ") + " Authentication", "Multi-Factor Identity Verification");
            
            // Step 1: Start login and show CAPTCHA
            ConsoleUI.printSection("Step 1: Security Challenge");
            String captchaCode = authService.startLogin();
            System.out.println("  CAPTCHA Code: " + ConsoleUI.BOLD + captchaCode + ConsoleUI.RESET + " (verification required)");
            
            ConsoleUI.printPrompt("Enter CAPTCHA (or 0 to Cancel)");
            String captchaResponse = scanner.nextLine().trim();
            if ("0".equals(captchaResponse) || "back".equalsIgnoreCase(captchaResponse)) {
                ConsoleUI.printInfo("Authentication canceled. Returned to Main Menu.");
                return;
            }
            
            if (!authService.validateCaptcha(captchaResponse)) {
                ConsoleUI.printError("CAPTCHA verification failed. Authentication canceled.");
                return;
            }
            ConsoleUI.printSuccess("CAPTCHA verified.");
            
            // Step 2: Username and Password
            ConsoleUI.printSection("Step 2: Account Credentials");
            ConsoleUI.printPrompt("Username (or 0 to Cancel)");
            String username = scanner.nextLine().trim();
            if ("0".equals(username) || "back".equalsIgnoreCase(username)) {
                ConsoleUI.printInfo("Authentication canceled. Returned to Main Menu.");
                return;
            }

            ConsoleUI.printPrompt("Password (or 0 to Cancel)");
            String password = scanner.nextLine().trim();
            if ("0".equals(password) || "back".equalsIgnoreCase(password)) {
                ConsoleUI.printInfo("Authentication canceled. Returned to Main Menu.");
                return;
            }
            
            if (!authService.verifyCredentials(username, password)) {
                ConsoleUI.printError("Invalid username or password. Login canceled.");
                return;
            }
            ConsoleUI.printSuccess("Credentials validated.");
            
            // Step 3: OTP Verification
            ConsoleUI.printSection("Step 3: Two-Factor OTP Verification");
            String otp = authService.getOTPForDisplay();
            System.out.println("  One-Time Password (OTP): " + ConsoleUI.BOLD + otp + ConsoleUI.RESET + " [Simulated SMS/Email Gateway]");
            System.out.println(ConsoleUI.DIM + "  Attempts remaining: " + authService.getOTPRemainingAttempts() + ConsoleUI.RESET);
            
            ConsoleUI.printPrompt("Enter 6-digit OTP (or 0 to Cancel)");
            String otpResponse = scanner.nextLine().trim();
            if ("0".equals(otpResponse) || "back".equalsIgnoreCase(otpResponse)) {
                ConsoleUI.printInfo("Authentication canceled. Returned to Main Menu.");
                return;
            }
            
            if (!authService.validateOTP(otpResponse)) {
                ConsoleUI.printError("OTP verification failed. Remaining attempts: " + authService.getOTPRemainingAttempts());
                return;
            }
            ConsoleUI.printSuccess("Identity successfully verified.");
            
            // Step 4: Complete login
            currentUser = authService.completeLogin(username);
            
            ConsoleUI.printCard("Active Session Info", Arrays.asList(
                    "User       : " + currentUser.getUsername(),
                    "Role       : " + currentUser.getRole().getDescription(),
                    "Authority  : " + currentUser.getRole().name()
            ));
            
            // Show role-based dashboard
            showRoleDashboard(currentUser.getRole().name());
            
        } catch (AuthenticationException e) {
            ConsoleUI.printError("Authentication Error: " + e.getMessage());
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
        ConsoleUI.printHeader("Customer Portal", "Logged in: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View Subscribed Services");
        ConsoleUI.printMenuOption("2", "Raise Trouble Ticket");
        ConsoleUI.printMenuOption("3", "View My Tickets");
        ConsoleUI.printMenuOption("4", "Track Ticket Status");
        ConsoleUI.printMenuOption("5", "View Ticket Audit History");
        ConsoleUI.printMenuOption("6", "View Notifications");
        ConsoleUI.printMenuOption("7", "Submit Service Feedback");
        ConsoleUI.printMenuOption("8", "Sign Out");
        
        ConsoleUI.printPrompt("Select option (1-8)");
        String choice = scanner.nextLine().trim();
        return customerController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Service Desk Administrator Dashboard
     */
    private static boolean showServiceDeskDashboard() {
        ConsoleUI.printHeader("Service Desk Operations Console", "Operator: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View Open Trouble Tickets");
        ConsoleUI.printMenuOption("2", "Assign Engineer (Auto / Manual)");
        ConsoleUI.printMenuOption("3", "Reassign Ticket");
        ConsoleUI.printMenuOption("4", "Escalate Ticket");
        ConsoleUI.printMenuOption("5", "Update Priority");
        ConsoleUI.printMenuOption("6", "Run Real-Time SLA Audit");
        ConsoleUI.printMenuOption("7", "Close Ticket");
        ConsoleUI.printMenuOption("8", "Generate Management Reports");
        ConsoleUI.printMenuOption("9", "Sign Out");
        
        ConsoleUI.printPrompt("Select option (1-9)");
        String choice = scanner.nextLine().trim();
        return serviceDeskController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Network Engineer Dashboard
     */
    private static boolean showEngineerDashboard() {
        ConsoleUI.printHeader("Network Engineer Workbench", "Engineer: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View Assigned Tickets");
        ConsoleUI.printMenuOption("2", "Update Ticket Status");
        ConsoleUI.printMenuOption("3", "Add Resolution & Root Cause");
        ConsoleUI.printMenuOption("4", "View Ticket Details");
        ConsoleUI.printMenuOption("5", "Check SLA Status");
        ConsoleUI.printMenuOption("6", "Sign Out");
        
        ConsoleUI.printPrompt("Select option (1-6)");
        String choice = scanner.nextLine().trim();
        return engineerController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Network Manager Dashboard
     */
    private static boolean showManagerDashboard() {
        ConsoleUI.printHeader("Network Operations Manager Dashboard", "Manager: " + currentUser.getUsername());
        ConsoleUI.printMenuOption("1", "View Operational Health Metrics");
        ConsoleUI.printMenuOption("2", "View Engineer Performance Matrix");
        ConsoleUI.printMenuOption("3", "Generate SLA Compliance Report");
        ConsoleUI.printMenuOption("4", "Generate Incident Analysis Report");
        ConsoleUI.printMenuOption("5", "Manage Escalation Queue");
        ConsoleUI.printMenuOption("6", "Sign Out");
        
        ConsoleUI.printPrompt("Select option (1-6)");
        String choice = scanner.nextLine().trim();
        return managerController.handleMenuChoice(choice, currentUser, scanner);
    }
}
