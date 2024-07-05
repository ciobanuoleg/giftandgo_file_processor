package com.example.demo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.FileProcessingResult;

import com.example.demo.repository.LoggingRepository;
import com.example.demo.repository.entity.DBLogStatement;
import com.example.demo.service.DBLoggerService;
import com.example.demo.util.RequestUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * DB Logger service inplementation class allowing to store file processing result in DB.
 */
@Service
public class DBLoggerServiceImpl implements DBLoggerService {
	
	@Autowired
	LoggingRepository repo;
	
	@Override
	public void log(long start, long stop, HttpServletRequest request, FileProcessingResult response){
		DBLogStatement logStatement = buildLogStatement(start, stop, request, response);
		repo.save(logStatement);
	}
	
	private DBLogStatement buildLogStatement(long start, long stop, HttpServletRequest request, FileProcessingResult response) {
		DBLogStatement logStatement = new DBLogStatement();
		
		logStatement.setTimeLapsed(stop - start);
		logStatement.setUri(request.getRequestURI());
		logStatement.setResponseCode(response.responseEntity().getStatusCode().value());
		logStatement.setUserIpAddress(RequestUtil.getRemoteAddress(request));	
		
		logStatement.setIpProvider( response.validationresult().isp() );
		logStatement.setCountryCode( response.validationresult().countryCode() );
		logStatement.setTimestamp(start);
		logStatement.setTimeLapsed(stop - start);
		
		return logStatement;
	}
	
}
