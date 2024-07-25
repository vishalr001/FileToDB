package com.app.filetodb.controller;

import com.app.filetodb.component.DataValidator;
import com.app.filetodb.component.ErrorHandler;
import com.app.filetodb.service.BatchDatabaseService;
import com.app.filetodb.service.CsvReaderService;
import com.app.filetodb.service.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/csv")
public class CsvController {

    @Autowired
    private CsvReaderService csvReaderService;

    @Autowired
    private DataValidator dataValidator;

    @Autowired
    private BatchDatabaseService batchDatabaseService;

    @Autowired
    private ErrorHandler errorHandler;

    @Autowired
    private ReportingService reportingService;

    @PostMapping("/upload")
    @Operation(
            summary = "Upload CSV file",
            description = "Uploads a CSV file and processes it to insert data into the database.",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary")
                    )
            )
    )
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please upload a CSV file.");
        }

        try (Stream<String[]> stream = csvReaderService.parseCSV(file)) {
            long st = System.currentTimeMillis();
            List<String[]> validRows = stream
                    .parallel()
                    .peek(row -> {
                        if (!dataValidator.isValid(row)) {
                            errorHandler.logError(row, new Exception("Validation failed"));
                            reportingService.recordFailure();
                        }
                    })
                    .filter(dataValidator::isValid)
                    .collect(Collectors.toList());
            // insert valid rows in db
            batchDatabaseService.insertData(validRows);
            // print summary
            reportingService.generateReport();
            //print time taken for insertion
            System.out.println("Time to execute all batches insertion "+
                    ((System.currentTimeMillis() - st) /1000)+"s");

            return ResponseEntity.ok("CSV processed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            errorHandler.logError(null, e);
            return ResponseEntity.status(500).body("Error processing CSV: " + e.getMessage());
        } finally {
            errorHandler.close();
        }
    }
}
