package com.example.demo.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

/**
 * Entity class for logging of file processing result.
 */
@Entity
public class DBLogStatement {
	// 1. Request Id – This can be simply a generated UUID.
	@jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	//2. Request Uri
	private String uri;
	
	//3. Request Timestamp – This should be the timestamp when the request reached the application
	private long timestamp;
	
	//4. HTTP Response code – 200, 403 , 500 etc
	private int responseCode;
	
	//5. Requset IP Address
	private String userIpAddress;
	
	//6. Request Country Code
	private String countryCode;
	
	//7. Request IP Provider – The provider (ISP) of the IP address
	private String ipProvider;
	
	//8. Time Lapsed – The time taken (in milli seconds) to complete the request
	private long timeLapsed;
	
	public DBLogStatement() {
	}
	
	public DBLogStatement(Long id, String uri, long timestamp, int responseCode, String userIpAddress,
			String countryCode, String ipProvider, long timeLapsed) {
		super();
		this.id = id;
		this.uri = uri;
		this.timestamp = timestamp;
		this.responseCode = responseCode;
		this.userIpAddress = userIpAddress;
		this.countryCode = countryCode;
		this.ipProvider = ipProvider;
		this.timeLapsed = timeLapsed;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public String getUserIpAddress() {
		return userIpAddress;
	}

	public void setUserIpAddress(String userIpAddress) {
		this.userIpAddress = userIpAddress;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getIpProvider() {
		return ipProvider;
	}

	public void setIpProvider(String ipProvider) {
		this.ipProvider = ipProvider;
	}

	public long getTimeLapsed() {
		return timeLapsed;
	}

	public void setTimeLapsed(long timeLapsed) {
		this.timeLapsed = timeLapsed;
	}

	@Override
	public String toString() {
		return "DBLogStatement [id=" + id + ", uri=" + uri + ", timestamp=" + timestamp + ", responseCode="
				+ responseCode + ", userIpAddress=" + userIpAddress + ", countryCode=" + countryCode + ", ipProvider="
				+ ipProvider + ", timeLapsed=" + timeLapsed + "]";
	}
}
