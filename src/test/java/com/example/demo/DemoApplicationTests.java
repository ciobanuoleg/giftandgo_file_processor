package com.example.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.example.demo.repository.LoggingRepository;
import com.example.demo.repository.entity.DBLogStatement;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

/**
 * Integration test for FileController with all possible scenarios of valid and invalid user ip and also valid/invalid input file.
 */

@ActiveProfiles(value = "integration")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WireMockTest(httpPort = 8082)
class DemoApplicationTests {
	
	private static final String PART_KEY_NAME = "file";

	private static final String WRONG_ENTRY_FILE = "WrongEntryFile.txt";

	private static final String ENTRY_FILE = "EntryFile.txt";

	private static final int FORBIDDEN_ERROR_CODE = 403;

	private static final int SERVER_ERROR_CODE = 500;

	private static final String EMPTY = "";

	private static final String BANNED_COUNTRY_MESSAGE = "IP Addresses from this country are not allowed!";

	private static final String BANNED_ISP_MESSAGE = "IP addresses from this Internet service provider or data centre are not allowed!";

	@Value(value="${local.server.port}")
    private int port;
	
	@Value("${server.url}")
	private String serverUrl;
	
	@Value("${ipValidation.pattern}")
	private String validationPattern;
	
	@Autowired
	LoggingRepository repo;
	
	
	@BeforeEach
	public void before(){
		serverUrl = serverUrl.replace("8081", "" + port);
	}

	@Test
	void testAllowedIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		String responseBody = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Canada",
				  "countryCode": "CA",
				  "region": "QC",
				  "regionName": "Quebec",
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
		
		// The static DSL will be automatically configured for you
		stubFor(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));
		
        // Instance DSL can be obtained from the runtime info parameter
        WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        //headers.set("User-Agent", "PostmanRuntime/7.36.3");
        
        org.springframework.util.MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        URL url = this.getClass().getClassLoader().getResource(ENTRY_FILE);
        Path path = Paths.get(url.toURI());
        body.add(PART_KEY_NAME, new FileSystemResource(path.toFile()));
        
        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);
       
       //call file processor controller
       RestTemplate restTemplate = new RestTemplate();
       ResponseEntity<String> response = restTemplate
         .postForEntity(serverUrl, requestEntity, String.class);
       //check if the result is successful
       Assertions.assertTrue(response.getStatusCode().value() == 200);
       Assertions.assertTrue(response.getBody().contains("John Smith"));
       
       // database log verification
       DBLogStatement logItem = repo.findLast();       
       Assertions.assertEquals("CA", logItem.getCountryCode());
       Assertions.assertEquals("Le Groupe Videotron Ltee", logItem.getIpProvider());
       Assertions.assertEquals(200, logItem.getResponseCode());
    }
	
	@Test
	void testSpainIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "88.26.241.248",
				  "status": "success",
				  "country": "Spain",
				  "countryCode": "ES",
				  "region": "CT",
				  "regionName": "Catalonia",
				  "city": "Salou",
				  "zip": "43840",
				  "lat": 41.071,
				  "lon": 1.1383,
				  "timezone": "Europe/Madrid",
				  "isp": "Telefonica de Espana SAU",
				  "org": "RIMA (Red IP Multi Acceso)",
				  "as": "AS3352 TELEFONICA DE ESPANA S.A.U."
				}
				""";
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
        // check the result status code is 403 and message content is appropriate
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == 403);
        Assertions.assertEquals(BANNED_COUNTRY_MESSAGE, statusCodeTextContainer.get(0));
        
        // database log verification
        DBLogStatement logItem = repo.findLast();       
        Assertions.assertEquals("ES", logItem.getCountryCode());
        Assertions.assertEquals("Telefonica de Espana SAU", logItem.getIpProvider());
        Assertions.assertEquals(FORBIDDEN_ERROR_CODE, logItem.getResponseCode());
	}
	
	@Test
	void testUsaIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "100.43.128.0",
				  "status": "success",
				  "country": "United States",
				  "countryCode": "US",
				  "region": "CA",
				  "regionName": "California",
				  "city": "La Palma",
				  "zip": "90623",
				  "lat": 33.8608,
				  "lon": -118.033,
				  "timezone": "America/Los_Angeles",
				  "isp": "Krypt Technologies",
				  "org": "Krypt Technologies",
				  "as": ""
				}
				""";
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == 403);
        Assertions.assertEquals(BANNED_COUNTRY_MESSAGE, statusCodeTextContainer.get(0));
	}
	
	@Test
	void testChinaIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "221.192.199.49",
				  "status": "success",
				  "country": "China",
				  "countryCode": "CN",
				  "region": "BJ",
				  "regionName": "Beijing",
				  "city": "Beijing",
				  "zip": "",
				  "lat": 39.911,
				  "lon": 116.395,
				  "timezone": "Asia/Shanghai",
				  "isp": "CNC Group CHINA169 Hebei Province Network",
				  "org": "",
				  "as": "AS4837 CHINA UNICOM China169 Backbone"
				}
				""";
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();

        

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == 403);
        Assertions.assertEquals(BANNED_COUNTRY_MESSAGE, statusCodeTextContainer.get(0));
	}
	
	@Test
	void testAWSIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
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
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();        

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == 403);
        Assertions.assertEquals(BANNED_ISP_MESSAGE, statusCodeTextContainer.get(0));
	}
	
	@Test
	void testGCPIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "34.155.255.24",
				  "status": "success",
				  "country": "France",
				  "countryCode": "FR",
				  "region": "IDF",
				  "regionName": "Île-de-France",
				  "city": "Paris",
				  "zip": "",
				  "lat": 48.8566,
				  "lon": 2.3522,
				  "timezone": "Europe/Paris",
				  "isp": "Google LLC",
				  "org": "Google Cloud (europe-west9)",
				  "as": "AS15169 Google LLC"
				}
				""";
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();

        

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == 403);
        Assertions.assertEquals(BANNED_ISP_MESSAGE, statusCodeTextContainer.get(0));
	}
	
	@Test
	void testAzureIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "40.79.129.1",
				  "status": "success",
				  "country": "France",
				  "countryCode": "FR",
				  "region": "IDF",
				  "regionName": "Île-de-France",
				  "city": "Paris",
				  "zip": "75000",
				  "lat": 48.856567,
				  "lon": 2.347322,
				  "timezone": "Europe/Paris",
				  "isp": "Microsoft Corporation",
				  "org": "Microsoft Azure SQL (francecentral)",
				  "as": "AS8075 Microsoft Corporation"
				}

				""";
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();

        

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == 403);
        Assertions.assertEquals(BANNED_ISP_MESSAGE, statusCodeTextContainer.get(0));
	}
	
	@Test
	void testWrongIP(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "40.79.1291",
				  "message": "invalid query",
				  "status": "fail"
				}
				""";
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildRequestEntity();

        

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == FORBIDDEN_ERROR_CODE);
        Assertions.assertEquals("invalid query", statusCodeTextContainer.get(0));
	}
	
	@Test
	void testWrongInputFile(WireMockRuntimeInfo wmRuntimeInfo) throws URISyntaxException, InterruptedException {
		
		
		List<Integer> statusCodeContainer = new ArrayList<>();
	    List<String> statusCodeTextContainer = new ArrayList<>();
		
	    String responseBody = """
				{
				  "query": "24.48.0.1",
				  "status": "success",
				  "country": "Canada",
				  "countryCode": "CA",
				  "region": "QC",
				  "regionName": "Quebec",
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
		
		WireMock wireMock = wmRuntimeInfo.getWireMock();
        wireMock.register(get(urlPathMatching(validationPattern + ".*")).willReturn(aResponse().withStatus(200).withBody(responseBody)));

        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity = buildWrongRequestEntity();

        

        RestTemplate restTemplate = buildRestTemplate(statusCodeContainer, statusCodeTextContainer);
       
        restTemplate.postForEntity(serverUrl, requestEntity, String.class);
       
       
        Assertions.assertTrue(statusCodeContainer.get(0).intValue() == SERVER_ERROR_CODE);
        Assertions.assertEquals(EMPTY, statusCodeTextContainer.get(0));
        
        // database log verification
        DBLogStatement logItem = repo.findLast();       
        Assertions.assertEquals("CA", logItem.getCountryCode());
        Assertions.assertEquals("Le Groupe Videotron Ltee", logItem.getIpProvider());
        Assertions.assertEquals(SERVER_ERROR_CODE, logItem.getResponseCode());
	}

	private HttpEntity<org.springframework.util.MultiValueMap<String, Object>> buildRequestEntity()
			throws URISyntaxException {
		return buildRequestEntity(ENTRY_FILE);
	}
	
	private HttpEntity<org.springframework.util.MultiValueMap<String, Object>> buildRequestEntity(String fileName)
			throws URISyntaxException {
		HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        org.springframework.util.MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        URL url = this.getClass().getClassLoader().getResource(fileName);
        Path path = Paths.get(url.toURI());
        body.add(PART_KEY_NAME, new FileSystemResource(path.toFile()));
        
        HttpEntity<org.springframework.util.MultiValueMap<String, Object>> requestEntity
        = new HttpEntity<>(body, headers);
		return requestEntity;
	}
	
	private HttpEntity<org.springframework.util.MultiValueMap<String, Object>> buildWrongRequestEntity()
			throws URISyntaxException {
		return buildRequestEntity(WRONG_ENTRY_FILE);
	}

	private RestTemplate buildRestTemplate(List<Integer> statusCodeContainer, List<String> statusCodeTextContainer) {
		RestTemplate restTemplate = new RestTemplate();
		   
		   restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
		       @Override
		       public void handleError(ClientHttpResponse response) throws IOException {
		    	   statusCodeContainer.add(response.getStatusCode().value());
		    	   		    	       		   
		    		   String text = EMPTY;
		    		   try{
		    			   BufferedReader reader = new BufferedReader( new InputStreamReader(response.getBody(), StandardCharsets.UTF_8));
		    			   text =  reader.lines().collect(Collectors.joining("\n"));
		    		   } catch(IOException e) {}
		    		           
		    		   statusCodeTextContainer.add(text);
		    	   
		       }
		   });
		return restTemplate;
	}
}
