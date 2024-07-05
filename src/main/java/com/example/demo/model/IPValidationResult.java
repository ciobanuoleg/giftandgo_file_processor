package com.example.demo.model;
/**
 * Record class that retains only the information we need internally for logging into DB.
 */
public record IPValidationResult(String message, boolean valid, String countryCode, String isp) {
}
