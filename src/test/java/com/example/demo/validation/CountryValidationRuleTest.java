package com.example.demo.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.model.IpValidationExternalResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test cases for validation of user ip by country. 
 */
@SpringBootTest
public class CountryValidationRuleTest {
	
	@Autowired
	private CountryValidationRule rule;
	
	@org.junit.jupiter.api.Test
	public void testValidateUSA() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "United states",
				  "countryCode": "US",
				  "region": "WA",
				  "regionName": "WA",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Le Groupe Videotron Ltee",
				  "org": "Videotron Ltee",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		org.junit.jupiter.api.Assertions.assertFalse(rule.isValid(record));
	}
	
	@org.junit.jupiter.api.Test
	public void testValidateChina() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "China",
				  "countryCode": "CN",
				  "region": "N/A",
				  "regionName": "N/A",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Le Groupe Videotron Ltee",
				  "org": "Videotron Ltee",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		org.junit.jupiter.api.Assertions.assertFalse(rule.isValid(record));
	}
	
	@org.junit.jupiter.api.Test
	public void testValidateSpain() throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Spain",
				  "countryCode": "ES",
				  "region": "N/A",
				  "regionName": "N/A",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Le Groupe Videotron Ltee",
				  "org": "Videotron Ltee",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		org.junit.jupiter.api.Assertions.assertFalse(rule.isValid(record));
	}
	
	@org.junit.jupiter.api.Test
	public void testValidateFrance() throws JsonMappingException, JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		
		String result = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "France",
				  "countryCode": "France",
				  "region": "N/A",
				  "regionName": "N/A",
				  "city": "Montreal",
				  "zip": "H1K",
				  "lat": 45.6085,
				  "lon": -73.5493,
				  "timezone": "America/Toronto",
				  "isp": "Le Groupe Videotron Ltee",
				  "org": "Videotron Ltee",
				  "as": "AS5769 Videotron Ltee"
				}
				""";
		
		IpValidationExternalResponse record = mapper.readValue(result, IpValidationExternalResponse.class);
		
		org.junit.jupiter.api.Assertions.assertTrue(rule.isValid(record));
	}
	

}
