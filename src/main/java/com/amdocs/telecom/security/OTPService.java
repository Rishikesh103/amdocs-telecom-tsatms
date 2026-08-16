package com.amdocs.telecom.security;

import java.util.Random;

/**
 * OTP (One-Time Password) service for two-factor authentication.
 * Generates 6-digit OTP codes with expiration.
 */
public class OTPService {
    
    private static final int OTP_LENGTH = 6;
    private static final Random RANDOM = new Random();
    private static final long OTP_EXPIRY_MILLIS = 10 * 60 * 1000; // 10 minutes
    
    private String otpCode;
    private long generatedTime;
    private int attemptCount;
    private static final int MAX_ATTEMPTS = 3;
    
    /**
     * Generates a new OTP.
     * In a real system, this would be sent via SMS/Email.
     */
    public void generateOTP() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        this.otpCode = sb.toString();
        this.generatedTime = System.currentTimeMillis();
        this.attemptCount = 0;
        
        // Print to console for demo purposes
        System.out.println("\n[SYSTEM] OTP generated: " + otpCode + " (Valid for 10 minutes)");
    }
    
    /**
     * Gets the current OTP code.
     * In production, this would not be exposed.
     * 
     * @return The OTP code
     */
    public String getOTPCode() {
        return otpCode;
    }
    
    /**
     * Validates the user's OTP response.
     * Tracks attempts and locks after MAX_ATTEMPTS failures.
     * 
     * @param userOTP The user's OTP response
     * @return true if valid and not expired, false otherwise
     */
    public boolean validateOTP(String userOTP) {
        if (otpCode == null || userOTP == null) {
            incrementAttempts();
            return false;
        }
        
        // Check expiry
        long currentTime = System.currentTimeMillis();
        if (currentTime - generatedTime > OTP_EXPIRY_MILLIS) {
            incrementAttempts();
            return false;
        }
        
        // Check attempt limit
        if (attemptCount >= MAX_ATTEMPTS) {
            return false;
        }
        
        if (otpCode.equals(userOTP)) {
            return true;
        }
        
        incrementAttempts();
        return false;
    }
    
    /**
     * Increments the failed attempt counter.
     */
    private void incrementAttempts() {
        attemptCount++;
    }
    
    /**
     * Gets remaining attempts.
     * 
     * @return Number of remaining attempts before lockout
     */
    public int getRemainingAttempts() {
        return Math.max(0, MAX_ATTEMPTS - attemptCount);
    }
    
    /**
     * Checks if OTP entry is locked.
     * 
     * @return true if max attempts reached, false otherwise
     */
    public boolean isLocked() {
        return attemptCount >= MAX_ATTEMPTS;
    }
    
    /**
     * Checks if OTP has expired.
     * 
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime - generatedTime > OTP_EXPIRY_MILLIS;
    }
}
