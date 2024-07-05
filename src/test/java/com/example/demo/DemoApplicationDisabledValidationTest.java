package com.example.demo;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

/**
 * Integration test for FileController with all disabled ip validation.
 */
@ActiveProfiles(value = "disablevalidation")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@WireMockTest(httpPort = 8082)
public class DemoApplicationDisabledValidationTest {
	
	private static final String PART_KEY_NAME = "file";

	private static final String ENTRY_FILE = "EntryFile.txt";
	
	@Value("${server.url}")
	private String serverUrl;
	
	@Value("${ipValidation.pattern}")
	private String validationPattern;
	
	@Test
	void testAllowedIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //headers.set("User-Agent", "PostmanRuntime/7.36.3");
        
        org.springframework.util.MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        URL url = this.getClass().getClassLoader().getResource(ENTRY_FILE);
        Path path = Paths.get(url.toURI());
        body.add(PART_KEY_NAME, new FileSystemResource(path.toFile()));
        
        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);

       RestTemplate restTemplate = new RestTemplate();
       ResponseEntity<String> response = restTemplate
         .postForEntity(serverUrl, requestEntity, String.class);
       Assertions.assertTrue(response.getStatusCode().value() == 200);
       Assertions.assertTrue(response.getBody().contains("John Smith"));
	}
}
