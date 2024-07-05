package com.example.demo.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.IpValidationExternalResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * test cases for validation of user ip by internet service provider.
 */
@SpringBootTest
public class ISPValidationRuleTest {
	
	@Autowired
	private ISPValidationRule rule;
	
	@Test
	public void testValidateAWS() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Canada",
				  "countryCode": "Canada",
				  "region": "Quebec",
				  "regionName": "Quebec",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Amazon",
				  "org": "Amazon",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		Assertions.assertFalse(rule.isValid(record));
	}
	
	@Test
	public void testValidateGCP() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Canada",
				  "countryCode": "Canada",
				  "region": "Quebec",
				  "regionName": "Quebec",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Google",
				  "org": "Google",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		Assertions.assertFalse(rule.isValid(record));
	}
	
	@Test
	public void testValidateAzure() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Canada",
				  "countryCode": "Canada",
				  "region": "Quebec",
				  "regionName": "Quebec",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Azure",
				  "org": "Azure",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		org.junit.jupiter.api.Assertions.assertFalse(rule.isValid(record));
	}
	
	@Test
	public void testValidateVideotron() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Canada",
				  "countryCode": "Canada",
				  "region": "Quebec",
				  "regionName": "Quebec",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Le Groupe Videotron Ltee",
				  "org": "Videotron",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		Assertions.assertTrue(rule.isValid(record));
	}

}
