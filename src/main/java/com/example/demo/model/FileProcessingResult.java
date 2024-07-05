package com.example.demo.model;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

/**
 * Record class that helps to carry client response in the form of response entity together with ip validation result.
 */
public record FileProcessingResult(IPValidationResult validationresult, ResponseEntity<InputStreamResource> responseEntity) {
	
}
