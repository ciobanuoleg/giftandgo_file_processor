package com.example.demo.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.example.demo.model.IpValidationExternalResponse;

/**
 * Rule class for validation of user ip address by internet service provider.
 */
public class ISPValidationRule extends ValidationRule implements InitializingBean {
	
	private Set<String> ispSet;

	@Value(value="${banned.isp.set}")
	private String isps;
	
	@Override
	public boolean isValid(IpValidationExternalResponse validationResult) {
		
		for(String ispItem : ispSet) {
			if(validationResult.org().toLowerCase().contains(ispItem)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String getMessageCode(){
		return "banned.isp";
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		ispSet = new HashSet<String>(Arrays.asList(isps.split(",")));
	}
}
