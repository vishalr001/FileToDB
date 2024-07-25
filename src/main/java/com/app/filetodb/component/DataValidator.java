package com.app.filetodb.component;

import org.springframework.stereotype.Component;

@Component
public class DataValidator {
    public boolean isValid(String[] row) {
        if (row == null || row.length == 0) {
            return false;
        }
        for (String field : row) {
            if (field == null || field.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
