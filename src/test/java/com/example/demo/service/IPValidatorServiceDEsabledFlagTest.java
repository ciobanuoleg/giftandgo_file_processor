package com.example.demo.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.demo.model.IPValidationResult;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IpValidator Service test for banned internet service provider with disabled validation flag.
 */
@ActiveProfiles(value = "disablevalidation")
@SpringBootTest
@WireMockTest(httpPort = 8082)
public class IPValidatorServiceDEsabledFlagTest {
	@Autowired
	IPValidatorService ipValidator;
	
	@Value("${ipValidation.pattern}")
	private String validationPattern;
	
	@Test
	public void testAWSIPWithDisabledValidationFlag(WireMockRuntimeInfo wmRuntimeInfo){
				
		String responseBody = """
				{
				  "query": "51.85.0.1",
				  "status": "success",
				  "country": "Israel",
				  "countryCode": "IL",
				  "region": "TA",
				  "regionName": "Tel Aviv",
				  "city": "Tel Aviv",
				  "zip": "",
				  "lat": 32.0803,
				  "lon": 34.7805,
				  "timezone": "Asia/Jerusalem",
				  "isp": "Amazon.com, Inc.",
				  "org": "Amazon Technologies Inc",
				  "as": "AS16509 Amazon.com, Inc."
				}
				""";
		
		// The static DSL will be automatically configured for you
		stubFor(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));
		
		// Instance DSL can be obtained from the runtime info parameter
		WireMock wireMock = wmRuntimeInfo.getWireMock();
		wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		
		Mockito.doReturn("51.85.0.1").when(request).getRemoteAddr();
		
		IPValidationResult result = ipValidator.isValid(request);
		Assertions.assertTrue(result.valid());
	}
}
