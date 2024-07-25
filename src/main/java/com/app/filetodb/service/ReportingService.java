package com.app.filetodb.service;

import org.springframework.stereotype.Service;

@Service
public class ReportingService {
    private int successCount = 0;
    private int failureCount = 0;

    public void recordSuccess() {
        successCount++;
    }

    public void recordFailure() {
        failureCount++;
    }

    public void generateReport() {
        System.out.println("Insertion Summary:");
        System.out.println("Success: " + successCount);
        System.out.println("Failures: " + failureCount);
    }
}
