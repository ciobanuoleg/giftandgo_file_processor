package com.example.demo.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.example.demo.model.IPValidationResult;
import com.example.demo.model.IpValidationExternalResponse;
import com.example.demo.service.IPValidatorService;
import com.example.demo.util.RequestUtil;
import com.example.demo.validation.ValidationRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;


/**
 * Implementation class for user ip validation against external service.
 */
public class IPValidatorServiceImpl implements IPValidatorService {

public static final Logger logger = LoggerFactory.getLogger(IPValidatorServiceImpl.class);
	
	Set<ValidationRule> validationRules;
	
	@Value("${ipValidation.url}")
	private String ipServiceUrl;
	
	@Value("${ipValidation.enabled}")
	private boolean enabled;
	
	public IPValidatorServiceImpl(Set<ValidationRule> validationRuleSet) {
		this.validationRules = validationRuleSet;
	}
	
	
	protected String consultExternalIpValidationService(String ipAddress) {
		String query = ipServiceUrl + ipAddress;
		
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "application/json");

		Map<String, String> params = new HashMap<>();
		
		HttpEntity<String> entity = new HttpEntity<>(headers);

		HttpEntity<String> response = restTemplate.exchange(query, HttpMethod.GET, entity, String.class, params);
		
		String resultString = response.getBody();
		
		return resultString;
			
		
	}
	
	protected IpValidationExternalResponse getValidationRecord(String stringVerificationRersult){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(stringVerificationRersult, IpValidationExternalResponse.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		} 
	}
	
	protected IPValidationResult  isValid(IpValidationExternalResponse ipValidation) {
		
		if("fail".equals(ipValidation.status())) {
			logger.error("IP validation result is false: " + ipValidation.toString());
			return new IPValidationResult(ipValidation.message(), false, ipValidation.countryCode(), ipValidation.isp());
		}
		for(ValidationRule rule : validationRules) {
			if(!rule.isValid(ipValidation)) {
				return new IPValidationResult(rule.getMessage(), false, ipValidation.countryCode(), ipValidation.isp());
			}
		}
		return new IPValidationResult(null, true, ipValidation.countryCode(), ipValidation.isp());
	}
	
	public IPValidationResult isValid(String address) {
		String externalResult = consultExternalIpValidationService(address);
		IpValidationExternalResponse recordResult = getValidationRecord(externalResult);
		return isValid(recordResult);
	}
	
	
	
	@Override
	public IPValidationResult isValid(HttpServletRequest request) {
		
		if(!enabled) {
			logger.error("IP validation is disabled, thus all IP addresses are considered valid!");
			return new IPValidationResult(null, true, null, null);
		}
		
		String address = RequestUtil.getRemoteAddress(request);
		
		return isValid(address);
	}


	private String getRemoteADdress(HttpServletRequest request) {
		// if the service resides behind an application load balancer
		String address = request.getHeader("X-Forwarded-For");
		
		if (address == null) {
			address = request.getRemoteAddr();
		}
		return address;
	}

}
