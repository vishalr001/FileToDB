package com.app.filetodb.component;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class ErrorHandler {

    private PrintWriter writer;

    public ErrorHandler() {
        try {
            writer = new PrintWriter(new FileWriter("error_log.txt"));
        } catch (IOException e) {
            // Handle exception
            e.printStackTrace();
        }
    }

    public void logError(String[] row, Exception e) {
        writer.println("Error processing row: || " + (row != null ? String.join(",", row) : "N/A") + " || " + e.getMessage());
        writer.flush();
    }

    public void close() {
        if (writer != null) {
            writer.close();
        }
    }
}
