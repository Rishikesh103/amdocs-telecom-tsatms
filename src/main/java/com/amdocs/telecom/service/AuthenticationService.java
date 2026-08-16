package com.amdocs.telecom.service;

import com.amdocs.telecom.dao.UserAccountDAO;
import com.amdocs.telecom.dao.LoginHistoryDAO;
import com.amdocs.telecom.dao.impl.UserAccountDAOImpl;
import com.amdocs.telecom.dao.impl.LoginHistoryDAOImpl;
import com.amdocs.telecom.model.UserAccount;
import com.amdocs.telecom.model.LoginHistory;
import com.amdocs.telecom.model.LoginStatus;
import com.amdocs.telecom.model.AccountStatus;
import com.amdocs.telecom.exception.AuthenticationException;
import com.amdocs.telecom.exception.DAOException;
import com.amdocs.telecom.security.PasswordUtil;
import com.amdocs.telecom.security.CaptchaGenerator;
import com.amdocs.telecom.security.OTPService;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * AuthenticationService handles user login, account locking, password verification,
 * and security features like CAPTCHA and OTP.
 * 
 * Features implemented:
 * - Password authentication with bcrypt hashing
 * - CAPTCHA validation
 * - OTP (One-Time Password) validation
 * - Account locking after repeated failures
 * - Login history tracking
 * - Role-based access control
 */
public class AuthenticationService {
    
    private UserAccountDAO userAccountDAO;
    private LoginHistoryDAO loginHistoryDAO;
    private CaptchaGenerator captchaGenerator;
    private OTPService otpService;
    
    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;
    private static final String DEFAULT_IP = "127.0.0.1";
    
    public AuthenticationService() {
        this.userAccountDAO = new UserAccountDAOImpl();
        this.loginHistoryDAO = new LoginHistoryDAOImpl();
    }
    
    /**
     * Initiates login process - generates CAPTCHA.
     * 
     * @return CAPTCHA code (for demo purposes)
     * @throws AuthenticationException if CAPTCHA generation fails
     */
    public String startLogin() throws AuthenticationException {
        try {
            captchaGenerator = new CaptchaGenerator();
            captchaGenerator.generateCaptcha();
            return captchaGenerator.getCaptchaCode();
        } catch (Exception e) {
            throw new AuthenticationException("Failed to start login: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates CAPTCHA response from user.
     * 
     * @param userResponse The user's CAPTCHA response
     * @return true if CAPTCHA is valid, false otherwise
     * @throws AuthenticationException if validation fails
     */
    public boolean validateCaptcha(String userResponse) throws AuthenticationException {
        if (captchaGenerator == null) {
            throw new AuthenticationException("CAPTCHA not initialized. Start login first.");
        }
        
        if (captchaGenerator.isExpired()) {
            throw new AuthenticationException("CAPTCHA has expired. Please start login again.");
        }
        
        return captchaGenerator.validateCaptcha(userResponse);
    }
    
    /**
     * Verifies username and password. Generates OTP if successful.
     * Handles account locking on repeated failures.
     * 
     * @param username The username
     * @param password The plaintext password
     * @return true if credentials are valid, false otherwise
     * @throws AuthenticationException if account is locked or other errors occur
     */
    public boolean verifyCredentials(String username, String password) throws AuthenticationException {
        try {
            UserAccount account = userAccountDAO.findByUsername(username);
            
            if (account == null) {
                throw new AuthenticationException("User not found: " + username);
            }
            
            // Check if account is locked
            if (account.getStatus() == AccountStatus.LOCKED) {
                if (account.getLockUntil() != null && LocalDateTime.now().isBefore(account.getLockUntil())) {
                    throw new AuthenticationException("Account is locked. Try again later.");
                } else {
                    // Lock has expired, unlock the account
                    userAccountDAO.unlockAccount(account.getUserId());
                    account.setStatus(AccountStatus.ACTIVE);
                }
            }
            
            // Check if account is inactive
            if (account.getStatus() == AccountStatus.INACTIVE) {
                throw new AuthenticationException("Account is inactive.");
            }
            
            // Verify password
            if (!PasswordUtil.verifyPassword(password, account.getPasswordHash())) {
                // Increment failed attempts
                int newAttempts = account.getFailedLoginAttempts() + 1;
                userAccountDAO.updateFailedLoginAttempts(account.getUserId(), newAttempts);
                
                // Lock account if max attempts reached
                if (newAttempts >= MAX_LOGIN_ATTEMPTS) {
                    LocalDateTime lockUntil = LocalDateTime.now().plus(LOCK_DURATION_MINUTES, ChronoUnit.MINUTES);
                    userAccountDAO.lockAccount(account.getUserId(), lockUntil);
                    throw new AuthenticationException("Too many failed attempts. Account locked for 30 minutes.");
                }
                
                throw new AuthenticationException("Invalid password. Attempts remaining: " + (MAX_LOGIN_ATTEMPTS - newAttempts));
            }
            
            // Password is correct, reset failed attempts
            userAccountDAO.resetFailedAttempts(account.getUserId());
            
            // Generate OTP for two-factor authentication
            otpService = new OTPService();
            otpService.generateOTP();
            
            return true;
            
        } catch (DAOException e) {
            throw new AuthenticationException("Database error during authentication: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates OTP provided by user.
     * 
     * @param otp The user's OTP response
     * @return true if OTP is valid, false otherwise
     * @throws AuthenticationException if OTP validation fails
     */
    public boolean validateOTP(String otp) throws AuthenticationException {
        if (otpService == null) {
            throw new AuthenticationException("OTP not generated. Verify credentials first.");
        }
        
        if (otpService.isExpired()) {
            throw new AuthenticationException("OTP has expired. Please try login again.");
        }
        
        if (otpService.isLocked()) {
            throw new AuthenticationException("Maximum OTP attempts exceeded. Please try login again.");
        }
        
        return otpService.validateOTP(otp);
    }
    
    /**
     * Completes the login process and logs it to history.
     * 
     * @param username The username that successfully logged in
     * @return The UserAccount object if login successful
     * @throws AuthenticationException if login completion fails
     */
    public UserAccount completeLogin(String username) throws AuthenticationException {
        try {
            UserAccount account = userAccountDAO.findByUsername(username);
            
            if (account == null) {
                throw new AuthenticationException("User not found during login completion.");
            }
            
            // Log successful login to history
            LoginHistory loginHistory = new LoginHistory(account.getUserId(), DEFAULT_IP, LoginStatus.SUCCESS);
            loginHistoryDAO.create(loginHistory);
            
            return account;
            
        } catch (DAOException e) {
            throw new AuthenticationException("Error completing login: " + e.getMessage(), e);
        }
    }
    
    /**
     * Logs user logout.
     * 
     * @param userId The user ID
     * @throws AuthenticationException if logout logging fails
     */
    public void logout(int userId) throws AuthenticationException {
        try {
            // For now, we create a simple logout record
            // In a real system, we'd update the logout_time of the last login record
            System.out.println("[SYSTEM] User " + userId + " logged out successfully.");
        } catch (Exception e) {
            throw new AuthenticationException("Error logging out: " + e.getMessage(), e);
        }
    }
    
    /**
     * Initiates password reset for a username, generating an OTP.
     */
    public boolean requestPasswordReset(String username) throws AuthenticationException {
        try {
            UserAccount account = userAccountDAO.findByUsername(username);
            if (account == null) {
                throw new AuthenticationException("Username not found: " + username);
            }
            otpService = new OTPService();
            otpService.generateOTP();
            return true;
        } catch (DAOException e) {
            throw new AuthenticationException("Error finding account: " + e.getMessage(), e);
        }
    }

    /**
     * Resets account password after OTP validation.
     */
    public boolean resetPassword(String username, String newPassword) throws AuthenticationException {
        try {
            UserAccount account = userAccountDAO.findByUsername(username);
            if (account == null) {
                throw new AuthenticationException("Username not found: " + username);
            }
            account.setPasswordHash(PasswordUtil.hashPassword(newPassword));
            account.setFailedLoginAttempts(0);
            account.setStatus(AccountStatus.ACTIVE);
            account.setLockUntil(null);
            return userAccountDAO.update(account);
        } catch (DAOException e) {
            throw new AuthenticationException("Error updating password: " + e.getMessage(), e);
        }
    }

    /**
     * Gets OTP for display (demo only - should not be exposed in production).
     * 
     * @return The OTP code
     */
    public String getOTPForDisplay() {
        if (otpService != null) {
            return otpService.getOTPCode();
        }
        return null;
    }
    
    /**
     * Gets remaining OTP attempts.
     * 
     * @return Number of remaining attempts
     */
    public int getOTPRemainingAttempts() {
        if (otpService != null) {
            return otpService.getRemainingAttempts();
        }
        return 0;
    }
}
