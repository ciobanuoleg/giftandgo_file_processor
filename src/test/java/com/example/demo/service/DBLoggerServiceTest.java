package com.example.demo.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.FileProcessingResult;
import com.example.demo.model.IPValidationResult;
import com.example.demo.repository.LoggingRepository;
import com.example.demo.repository.entity.DBLogStatement;

import jakarta.servlet.http.HttpServletRequest;

/**
 * test class for logging in DB file processing result. 
 */
@ActiveProfiles(value = "integration")
@SpringBootTest
public class DBLoggerServiceTest {

	private static final String REQUEST_URI = "/v1/file";

	private static final String IP_PROVIDER = "Amazon LTD";

	private static final String COUNTRY_CODE = "CA";

	private static final String USER_IP_ADDRESS = "88.26.241.248";

	@Autowired
	DBLoggerService service;
	
	@Autowired
	LoggingRepository repo;
	
	@Test
	public void testLoggingToDB(){
		
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.doReturn(USER_IP_ADDRESS).when(request).getRemoteAddr();
		Mockito.doReturn(REQUEST_URI).when(request).getRequestURI();
		
		IPValidationResult validationresult = new IPValidationResult("All good", true, COUNTRY_CODE, IP_PROVIDER);
		ResponseEntity<InputStreamResource> responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() ;
		
		FileProcessingResult response = new FileProcessingResult(validationresult, responseEntity);
		
		long start = System.currentTimeMillis();
		service.log(System.currentTimeMillis(), System.currentTimeMillis()+1, request, response);
		
		DBLogStatement statement = repo.findLast();
		
		Assertions.assertEquals(USER_IP_ADDRESS, statement.getUserIpAddress());
		Assertions.assertEquals(COUNTRY_CODE, statement.getCountryCode());
		Assertions.assertEquals(IP_PROVIDER, statement.getIpProvider());
		Assertions.assertEquals(REQUEST_URI, statement.getUri());
		Assertions.assertEquals(start, statement.getTimestamp());
		Assertions.assertEquals(1l, statement.getTimeLapsed());
		Assertions.assertNotNull(statement.getId());
		Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), statement.getResponseCode());
		
		
		
	}
}
