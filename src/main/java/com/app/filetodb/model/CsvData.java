package com.app.filetodb.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "csv_data")
public class CsvData {
    @Id
    private Long id;
    private String description;
    private String name;
    private String value_1;
    private String value_2;
    private String value_3;
    private String value_4;
    private String company;
    private String item;
    private String value_5;
}
