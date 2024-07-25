package com.app.filetodb.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "CSV API", version = "1.0", description = "CSV File Processing API"))
public class SwaggerConfig {
}
