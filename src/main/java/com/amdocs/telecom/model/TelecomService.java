package com.amdocs.telecom.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a Telecom Service associated with a Customer.
 */
public class TelecomService {
    private int serviceId;
    private String serviceCode;
    private String serviceName;
    private ServiceType serviceType;
    private int customerId;
    private LocalDate activationDate;
    private EntityStatus serviceStatus;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    // ========== Constructors ==========
    public TelecomService() {
    }

    public TelecomService(String serviceCode, String serviceName, ServiceType serviceType,
                          int customerId, LocalDate activationDate) {
        this.serviceCode = serviceCode;
        this.serviceName = serviceName;
        this.serviceType = serviceType;
        this.customerId = customerId;
        this.activationDate = activationDate;
        this.serviceStatus = EntityStatus.ACTIVE;
        this.createdDate = LocalDateTime.now();
        this.updatedDate = LocalDateTime.now();
    }

    // ========== Getters & Setters ==========
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public EntityStatus getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(EntityStatus serviceStatus) {
        this.serviceStatus = serviceStatus;
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
        return "TelecomService{" +
                "serviceId=" + serviceId +
                ", serviceCode='" + serviceCode + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType=" + serviceType +
                ", customerId=" + customerId +
                ", activationDate=" + activationDate +
                ", serviceStatus=" + serviceStatus +
                ", createdDate=" + createdDate +
                ", updatedDate=" + updatedDate +
                '}';
    }
}
