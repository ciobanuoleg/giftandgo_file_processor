package com.example.demo.service;

import com.example.demo.model.FileProcessingResult;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for logging of DB processing result
 */
public interface DBLoggerService {
	public void log(long start, long stop, HttpServletRequest request, FileProcessingResult response);
}
