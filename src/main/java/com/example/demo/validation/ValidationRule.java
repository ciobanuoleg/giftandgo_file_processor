package com.example.demo.validation;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.LocaleResolver;

import com.example.demo.model.IpValidationExternalResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Basic class for validation of user ip address. Contains common code for ip validation.
 */
public abstract class ValidationRule {
	
	@Autowired
	LocaleResolver localeResolver;
	
	@Autowired
	HttpServletRequest request;
	
	@Autowired
	private MessageSource messageSource;
	
	public abstract boolean isValid(IpValidationExternalResponse validationResult);
	
	public abstract String getMessageCode();
	
	public String getMessage(){
		Locale locale = localeResolver.resolveLocale(request);
		return messageSource.getMessage(getMessageCode(), null, locale);
	}

	public void setLocaleResolver(LocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}
}
