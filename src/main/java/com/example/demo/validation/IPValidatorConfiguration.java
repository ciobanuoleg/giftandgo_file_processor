package com.example.demo.validation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.example.demo.service.impl.IPValidatorServiceImpl;

/**
 * Spring configuration class that helps to build spring beans with more sophisticated dependencies.
 */

@Configuration
public class IPValidatorConfiguration implements WebMvcConfigurer{

	private static final String LANG_PARAM = "lang";

	@Bean
	public ValidationRule getCountryRule(){
		return new CountryValidationRule();
	}
	
	@Bean
	public ValidationRule getISPRule(){
		return new ISPValidationRule();
	}
	
	@Bean
	public IPValidatorServiceImpl getIpValidator() {
		Set<ValidationRule> validationRuleSet = new HashSet<>(Arrays.asList(new ValidationRule[] { getCountryRule(), getISPRule()}));
		IPValidatorServiceImpl validator = new IPValidatorServiceImpl(validationRuleSet);
		return validator;
	}
	
	@Bean
	public LocaleResolver localeResolver() {
	    SessionLocaleResolver slr = new SessionLocaleResolver();
	    slr.setDefaultLocale(Locale.US);
	    return slr;
	}
	
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
	    LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
	    lci.setParamName(LANG_PARAM);
	    return lci;
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(localeChangeInterceptor());
	}
	
}
