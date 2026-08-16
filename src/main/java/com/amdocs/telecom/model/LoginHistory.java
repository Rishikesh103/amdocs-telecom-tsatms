package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity tracking user login attempts and history.
 */
public class LoginHistory {
    private int loginId;
    private int userId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private String ipAddress;
    private LoginStatus status;

    // ========== Constructors ==========
    public LoginHistory() {
    }

    public LoginHistory(int userId, String ipAddress, LoginStatus status) {
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.status = status;
        this.loginTime = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(LocalDateTime logoutTime) {
        this.logoutTime = logoutTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LoginStatus getStatus() {
        return status;
    }

    public void setStatus(LoginStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "LoginHistory{" +
                "loginId=" + loginId +
                ", userId=" + userId +
                ", loginTime=" + loginTime +
                ", status=" + status +
                '}';
    }
}
