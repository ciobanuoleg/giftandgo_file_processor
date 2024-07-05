package com.example.demo.model;

/**
 * Record class for catching result from external ip validation service.
 */
public record IpValidationExternalResponse(String message, String query, String status, String country, String countryCode, String region, String regionName, String city, String zip, double lat, double lon, String timezone, String isp, String org, String as) {
}
