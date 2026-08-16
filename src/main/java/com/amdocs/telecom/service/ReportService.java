package com.amdocs.telecom.service;

import com.amdocs.telecom.exception.DAOException;

public interface ReportService {
    String generateSlaComplianceReport(String format) throws DAOException;
    String generateEngineerPerformanceReport(String format) throws DAOException;
    String generateIncidentAnalysisReport(String format) throws DAOException;
}
