package com.example.demo.service;

import com.example.demo.model.IPValidationResult;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Service interface for validation of user ip address.
 */
public interface IPValidatorService {
	
	public IPValidationResult isValid(HttpServletRequest request);

}
