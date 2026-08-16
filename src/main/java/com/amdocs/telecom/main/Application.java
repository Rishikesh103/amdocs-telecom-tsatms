package com.amdocs.telecom.main;

import com.amdocs.telecom.controller.*;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.scheduler.NetworkEventProcessor;
import com.amdocs.telecom.scheduler.SLAMonitorScheduler;
import com.amdocs.telecom.service.AuthenticationService;
import com.amdocs.telecom.util.DBConnection;

import java.util.Scanner;

/**
 * Main application entry point for TSATMS (Telecom Service Assurance & Trouble Ticket Management System).
 * 
 * Default test credentials:
 * Customer: cust100245 / password123
 * Service Desk: admin_sd1 / password123
 * Engineer: eng1008 / password123
 * Manager: manager_nm1 / password123
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
            System.out.println("========================================================");
            System.out.println("TELECOM SERVICE ASSURANCE & TROUBLE TICKET MANAGEMENT");
            System.out.println("========================================================");
            System.out.println();
            
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
            } catch (Exception e) {
                System.out.println("Background services initialized.");
            }
            
            // Show main menu
            showMainMenu();
            
        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
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
            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Customer Login");
            System.out.println("2. Service Desk Login");
            System.out.println("3. Network Engineer Login");
            System.out.println("4. Network Manager Login");
            System.out.println("5. Exit");
            System.out.print("\nEnter your choice: ");
            
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
                    System.out.println("\nThank you for using TSATMS. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    /**
     * Attempts to authenticate a user through the login process.
     * Flow: CAPTCHA → Credentials → OTP → Role Dashboard
     */
    private static void attemptLogin(String roleName) {
        try {
            System.out.println("\n========== " + roleName + " LOGIN ==========");
            
            // Step 1: Start login and show CAPTCHA
            System.out.println("\nStep 1: CAPTCHA Verification");
            String captchaCode = authService.startLogin();
            System.out.println("CAPTCHA Code: " + captchaCode + " (for demo purposes)");
            System.out.print("Enter CAPTCHA: ");
            String captchaResponse = scanner.nextLine().trim();
            
            if (!authService.validateCaptcha(captchaResponse)) {
                System.out.println("❌ CAPTCHA validation failed. Login canceled.");
                return;
            }
            System.out.println("✓ CAPTCHA validated successfully.");
            
            // Step 2: Username and Password
            System.out.println("\nStep 2: Credentials Verification");
            System.out.print("Enter username: ");
            String username = scanner.nextLine().trim();
            System.out.print("Enter password: ");
            String password = scanner.nextLine().trim();
            
            if (!authService.verifyCredentials(username, password)) {
                System.out.println("❌ Invalid credentials. Login canceled.");
                return;
            }
            System.out.println("✓ Credentials verified successfully.");
            
            // Step 3: OTP Verification
            System.out.println("\nStep 3: OTP Verification");
            String otp = authService.getOTPForDisplay();
            System.out.println("OTP sent to registered mobile/email: " + otp + " (for demo)");
            System.out.println("Remaining attempts: " + authService.getOTPRemainingAttempts());
            System.out.print("Enter OTP: ");
            String otpResponse = scanner.nextLine().trim();
            
            if (!authService.validateOTP(otpResponse)) {
                System.out.println("❌ OTP validation failed. Remaining attempts: " + authService.getOTPRemainingAttempts());
                return;
            }
            System.out.println("✓ OTP validated successfully.");
            
            // Step 4: Complete login
            currentUser = authService.completeLogin(username);
            System.out.println("✓ LOGIN SUCCESSFUL");
            System.out.println("Welcome, " + currentUser.getUsername() + " (" + currentUser.getRole().getDescription() + ")");
            
            // Show role-based dashboard
            showRoleDashboard(currentUser.getRole().name());
            
        } catch (AuthenticationException e) {
            System.out.println("❌ Authentication Error: " + e.getMessage());
        }
    }
    
    /**
     * Displays role-specific dashboard and delegates choice handling to controllers.
     */
    private static void showRoleDashboard(String role) {
        boolean inDashboard = true;
        
        while (inDashboard) {
            System.out.println("\n========== " + role + " DASHBOARD ==========");
            
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
        System.out.println("1. View My Services");
        System.out.println("2. Raise Trouble Ticket");
        System.out.println("3. View My Tickets");
        System.out.println("4. Track Ticket");
        System.out.println("5. View Ticket History");
        System.out.println("6. View Notifications");
        System.out.println("7. Submit Feedback");
        System.out.println("8. Logout");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine().trim();
        return customerController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Service Desk Administrator Dashboard
     */
    private static boolean showServiceDeskDashboard() {
        System.out.println("1. View Open Tickets");
        System.out.println("2. Assign Engineer");
        System.out.println("3. Reassign Ticket");
        System.out.println("4. Escalate Ticket");
        System.out.println("5. Update Priority");
        System.out.println("6. Monitor SLA");
        System.out.println("7. Close Ticket");
        System.out.println("8. Generate Reports");
        System.out.println("9. Logout");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine().trim();
        return serviceDeskController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Network Engineer Dashboard
     */
    private static boolean showEngineerDashboard() {
        System.out.println("1. View Assigned Tickets");
        System.out.println("2. Update Ticket Status");
        System.out.println("3. Add Resolution");
        System.out.println("4. View Ticket Details");
        System.out.println("5. Check SLA Status");
        System.out.println("6. Logout");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine().trim();
        return engineerController.handleMenuChoice(choice, currentUser, scanner);
    }
    
    /**
     * Network Manager Dashboard
     */
    private static boolean showManagerDashboard() {
        System.out.println("1. View Dashboard Metrics");
        System.out.println("2. View Engineer Performance");
        System.out.println("3. Generate SLA Report");
        System.out.println("4. Generate Incident Report");
        System.out.println("5. Manage Escalations");
        System.out.println("6. Logout");
        System.out.print("\nEnter your choice: ");
        
        String choice = scanner.nextLine().trim();
        return managerController.handleMenuChoice(choice, currentUser, scanner);
    }
}
