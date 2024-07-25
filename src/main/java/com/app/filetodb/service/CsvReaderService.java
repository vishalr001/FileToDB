package com.app.filetodb.service;

import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.stream.Stream;

@Service
public class CsvReaderService {
    public Stream<String[]> parseCSV(MultipartFile file) {
        try {
            CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()));
            return reader.readAll().stream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Stream.empty();
    }
}
