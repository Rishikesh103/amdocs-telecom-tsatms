package com.amdocs.telecom.model;

import java.time.LocalDateTime;

/**
 * Entity representing a Customer in the Telecom Service system.
 * This is a Core model entity with proper encapsulation and Javadoc.
 */
public class Customer {
    private int customerId;
    private String customerNumber;
    private String customerName;
    private String email;
    private String mobileNumber;
    private CustomerType customerType;
    private String city;
    private EntityStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // ========== Constructors ==========
    public Customer() {
    }

    public Customer(String customerNumber, String customerName, String email,
                    String mobileNumber, CustomerType customerType, String city) {
        this.customerNumber = customerNumber;
        this.customerName = customerName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.customerType = customerType;
        this.city = city;
        this.status = EntityStatus.ACTIVE;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // ========== Getters & Setters (Encapsulation) ==========
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public EntityStatus getStatus() {
        return status;
    }

    public void setStatus(EntityStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerNumber='" + customerNumber + '\'' +
                ", customerName='" + customerName + '\'' +
                ", email='" + email + '\'' +
                ", mobileNumber='" + mobileNumber + '\'' +
                ", customerType=" + customerType +
                ", city='" + city + '\'' +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
