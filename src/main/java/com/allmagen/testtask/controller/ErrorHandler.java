package com.allmagen.testtask.controller;

import com.opencsv.exceptions.CsvValidationException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ErrorHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LogManager.getLogger(ErrorHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<String> handleRuntimeException(final RuntimeException ex,
                                                               final WebRequest request) {
        LOGGER.log(Level.ERROR, ex);
        String errorMessage = "Error: " + ex.getMessage();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(CsvValidationException.class)
    public final ResponseEntity<String> handleCsvValidationException(final CsvValidationException ex,
                                                                     final WebRequest request) {
        String errorMessage = "Upload from file failed";

        LOGGER.log(Level.ERROR, errorMessage);
        LOGGER.log(Level.ERROR, ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(IOException.class)
    public final ResponseEntity<String> handleIOException(final IOException ex,
                                                          final WebRequest request) {
        LOGGER.log(Level.ERROR, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
