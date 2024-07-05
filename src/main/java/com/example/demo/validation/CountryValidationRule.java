package com.example.demo.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.example.demo.model.IpValidationExternalResponse;
/**
 * A rule that helps to validate user ip address by country.
 */

public class CountryValidationRule extends ValidationRule implements InitializingBean {

	private static final String BANNED_COUNTRY = "banned.country";
	
	public Set<String> countryCodeSet;
	
	@Value("${banned.country.code.set}")
	String  countryCodes;
	
	public void afterPropertiesSet() {
		countryCodeSet = new HashSet<String>(Arrays.asList(countryCodes.split(",")));
	}
	
	@Override
	public boolean isValid(IpValidationExternalResponse validationResult) {
		return !countryCodeSet.contains(validationResult.countryCode().toLowerCase());
	}
	
	@Override
	public String getMessageCode(){
		return BANNED_COUNTRY;
	}
	
}
