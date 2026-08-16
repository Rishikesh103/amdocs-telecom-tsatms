package com.amdocs.telecom.security;

import java.util.Random;

/**
 * CAPTCHA generator for login security.
 * Generates simple alphanumeric challenges.
 */
public class CaptchaGenerator {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CAPTCHA_LENGTH = 6;
    private static final Random RANDOM = new Random();
    
    private String captchaCode;
    private long generatedTime;
    private static final long CAPTCHA_EXPIRY_MILLIS = 5 * 60 * 1000; // 5 minutes
    
    /**
     * Generates a new CAPTCHA code.
     */
    public void generateCaptcha() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        this.captchaCode = sb.toString();
        this.generatedTime = System.currentTimeMillis();
    }
    
    /**
     * Gets the current CAPTCHA code.
     * 
     * @return The CAPTCHA code
     */
    public String getCaptchaCode() {
        return captchaCode;
    }
    
    /**
     * Validates the user's CAPTCHA response.
     * 
     * @param userResponse The user's response
     * @return true if valid and not expired, false otherwise
     */
    public boolean validateCaptcha(String userResponse) {
        if (captchaCode == null || userResponse == null) {
            return false;
        }
        
        // Check expiry
        long currentTime = System.currentTimeMillis();
        if (currentTime - generatedTime > CAPTCHA_EXPIRY_MILLIS) {
            return false;
        }
        
        // Case-insensitive comparison
        return captchaCode.equalsIgnoreCase(userResponse);
    }
    
    /**
     * Checks if CAPTCHA has expired.
     * 
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime - generatedTime > CAPTCHA_EXPIRY_MILLIS;
    }
}
